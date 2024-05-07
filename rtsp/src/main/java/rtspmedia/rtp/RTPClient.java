package rtspmedia.rtp;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.*;

import merrimackutil.json.JsonIO;
import merrimackutil.json.types.JSONObject;
import rtspmedia.Client.ClientConfiguration;

import javax.sound.sampled.*;
/**
 * The RTPClient class handles the reception and playback of audio data streamed over RTP from a server.
 */
public class RTPClient {
    private JFrame frame;
    private JButton playButton;
    private JProgressBar progressBar;
    private JLabel albumCoverLabel;
    private JLabel songTitleLabel;
    private boolean isPlaying = false;
    private SourceDataLine line;
    private DatagramSocket socket;
    private Thread receiveThread;
    private InetAddress serverAddress;
    private int serverPort = 25000;
    private long totalDuration = 300000; // Example total duration in milliseconds for a 5-minute audio
    private static JSONObject configobj;
    private static ClientConfiguration config;
    private String configFile = "data/client-config/config.json";

    
    /**
     * Retrieves the total duration of the audio stream.
     * @return The total duration in milliseconds.
     */
    public long getTotalDuration() {
        return totalDuration;
    }

    
    /**
     * Sets the total duration of the audio stream.
     * @param totalDuration The duration in milliseconds.
     */
    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    private long currentPlaybackTime = 0; // Tracks the current playback time in milliseconds
    /**
     * Constructor that initializes the client with specific connection parameters.
     * @param port The port number to connect to the server.
     * @param length The total duration of the audio stream.
     * @param image Base64 encoded image for the album cover.
     * @param title The title of the song.
     * @throws LineUnavailableException if the system does not support the audio line.
     * @throws SocketException if there is an error creating the socket.
     * @throws UnknownHostException if the IP address of the host could not be determined.
     */
    public RTPClient() throws LineUnavailableException, SocketException, UnknownHostException {
        try {
            configobj = JsonIO.readObject(new File(configFile));
        } catch (FileNotFoundException x) {
            System.out.println("Couldn't find config file!");
        }
        try {
            config = new ClientConfiguration(configobj);
        } catch (InvalidObjectException x) {
            System.out.println("Invalid JSON Object");
        }

        socket = new DatagramSocket();
        serverAddress = InetAddress.getByName(config.getServerAddress());
        initializeAudio();
        initializeGUI();
        setAlbumCover(null); // Set default album cover
        setSongTitle(null); // Set default song title
    }
    /**
     * Constructor that initializes the client with specific connection parameters but without album cover and title.
     * @param port The port number to connect to the server.
     * @param length The total duration of the audio stream.
     * @throws LineUnavailableException if the system does not support the audio line.
     * @throws SocketException if there is an error creating the socket.
     * @throws UnknownHostException if the IP address of the host could not be determined.
     */
    public RTPClient(int port ) throws LineUnavailableException, SocketException, UnknownHostException {
        try {
            configobj = JsonIO.readObject(new File(configFile));
        } catch (FileNotFoundException x) {
            System.out.println("Couldn't find config file!");
        }
        try {
            config = new ClientConfiguration(configobj);
        } catch (InvalidObjectException x) {
            System.out.println("Invalid JSON Object");
        }

        this.serverPort=port;
        socket = new DatagramSocket();
        serverAddress = InetAddress.getByName("localhost");
        initializeAudio();
        initializeGUI();
        setAlbumCover(null); // Set default album cover
        setSongTitle(null); // Set default song title
    }

    public RTPClient(int port,long length ) throws LineUnavailableException, SocketException, UnknownHostException {
        try {
            configobj = JsonIO.readObject(new File(configFile));
        } catch (FileNotFoundException x) {
            System.out.println("Couldn't find config file!");
        }
        try {
            config = new ClientConfiguration(configobj);
        } catch (InvalidObjectException x) {
            System.out.println("Invalid JSON Object");
        }

        this.totalDuration=length;
        this.serverPort=port;
        socket = new DatagramSocket();
        serverAddress = InetAddress.getByName("localhost");
        initializeAudio();
        initializeGUI();
        setAlbumCover(null); // Set default album cover
        setSongTitle(null); // Set default song title
    }


    public RTPClient(int port,long length,String image,String title ) throws LineUnavailableException, SocketException, UnknownHostException {
        try {
            configobj = JsonIO.readObject(new File(configFile));
        } catch (FileNotFoundException x) {
            System.out.println("Couldn't find config file!");
        }
        try {
            config = new ClientConfiguration(configobj);
        } catch (InvalidObjectException x) {
            System.out.println("Invalid JSON Object");
        }

        this.albumCoverLabel=new JLabel();
        this.songTitleLabel = new JLabel();
        this.totalDuration=length;
        this.serverPort=port;
        this.setAlbumCover(image);
        this.setSongTitle(title);

        socket = new DatagramSocket();
        serverAddress = InetAddress.getByName("localhost");
        initializeAudio();
        initializeGUI();

    }
   /**
     * Initializes the graphical user interface components for the RTP client.
     * This includes setting up the frame, buttons, labels, and progress bar.
     */
    private void initializeGUI() {
        frame = new JFrame("RTP Audio Client");
        playButton = new JButton("Play");
        progressBar = new JProgressBar(0, 100); // Max 100 for percentage
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        JButton tearDownButton = new JButton("Tear Down"); // New button for tearing down the connection
        frame.getContentPane().add(albumCoverLabel, BorderLayout.WEST);
        frame.getContentPane().add(songTitleLabel, BorderLayout.SOUTH);
        frame.getContentPane().add(tearDownButton, BorderLayout.EAST); // Add the tear down button to the GUI
        tearDownButton.addActionListener(e -> {
            if (isPlaying) {
                stop(); // Stop the audio line and set isPlaying to false
            }
            if (socket != null && !socket.isClosed()) {
                socket.close(); // Close the socket
            }
            if (receiveThread != null && receiveThread.isAlive()) {
                receiveThread.interrupt(); // Stop the receiving thread
            }
        });

        playButton.addActionListener(e -> {
            if (isPlaying) {
                stop(); // Stop the audio line and set isPlaying to false
                playButton.setText("Play");
            } else {
                playButton.setText("Pause");
                isPlaying = true; // Set playing to true
                try {
                    if (currentPlaybackTime == 0) {
                        // First play or after the stream has ended
                        sendConnectionRequest(); // Send connection request only on first play
                    } else {
                        // Resume playback if currently paused and not at the beginning
                        line.start(); // Resume the audio line if it was paused
                    }
                    if (receiveThread == null || !receiveThread.isAlive()) {
                        // Start receiving thread if not already running
                        startReceiving();
                    }
                } catch (IOException ex) {
                    System.out.println("Error resuming audio playback: " + ex.getMessage());
                    isPlaying = false;
                    playButton.setText("Play");
                }
            }
        });
        

        frame.getContentPane().add(playButton, BorderLayout.NORTH);
        frame.getContentPane().add(progressBar, BorderLayout.CENTER);

// Add a new panel for the Tear Down button at the bottom
JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
bottomPanel.add(tearDownButton);
frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

// Define the action for the Tear Down button
tearDownButton.addActionListener(e -> {
    if (isPlaying) {
        stop(); // Stop the audio line and set isPlaying to false
    }
    if (socket != null && !socket.isClosed()) {
        socket.close(); // Close the socket
    }
    if (receiveThread != null && receiveThread.isAlive()) {
        receiveThread.interrupt(); // Stop the receiving thread
    }
});

        frame.setSize(400, 400); // Set the frame size to three times the length and twice the width
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    
    /**
     * Initializes the audio system for playback.
     * @throws LineUnavailableException if the system does not support the audio line.
     */
    private void initializeAudio() throws LineUnavailableException {
        AudioFormat format = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            48000.0f,
            16,
            2,
            4,
            48000.0f,
            false
        );
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format, 10000); // Buffer size for playback
    }

    private void sendConnectionRequest() throws IOException {
        // Check if the socket exists and is not closed; otherwise, create a new one
        if (socket == null || socket.isClosed()) {
            socket = new DatagramSocket(); // Create a new socket
        }
        byte[] message = "Hello, Server!".getBytes();
        DatagramPacket packet = new DatagramPacket(message, message.length, serverAddress, serverPort);
        socket.send(packet); // Send a connection request to the server
        if (!isPlaying) {
            startReceiving();
        }
    }
    
    private void stop() {
        isPlaying = false;
        if (line != null) {
            line.stop();
            line.flush();
            // Only close the line, do not close the socket here
        }
        // Do not set receiveThread to null or close the socket here
    }
    

    private void startReceiving() {
        receiveThread = new Thread(() -> {
            byte[] buf = new byte[4096];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            line.start();
            while (isPlaying) {
                try {
                    socket.receive(packet);
                    byte[] audioData = Arrays.copyOfRange(packet.getData(), 12, packet.getLength());
                    line.write(audioData, 0, audioData.length); // Play the received audio data
                    updatePlaybackTime(audioData.length); // Update playback time and progress bar
                } catch (IOException e) {
                    System.out.println("Error in receiving packet: " + e.getMessage());
                }
            }
        });
        receiveThread.start();
    }

    private void updatePlaybackTime(int audioDataLength) {
        // Calculate the duration of the audio data in milliseconds
        double duration = (audioDataLength / (double) line.getFormat().getFrameSize()) / line.getFormat().getFrameRate() * 1000;
        currentPlaybackTime += duration;
        updateProgress(currentPlaybackTime); // Update the progress bar based on the current playback time
    }

    private void updateProgress(long playbackTime) {
        int progress = (int)((playbackTime * 100) / totalDuration); // Calculate progress percentage
        progressBar.setValue(progress*2);
    }

   

    public void setAlbumCover(String base64Image) {
        if (base64Image == null) {
            InputStream is = getClass().getResourceAsStream("/images/placeholder.jpg");
            try {
                byte[] imageBytes = is.readAllBytes();
                base64Image = Base64.getEncoder().encodeToString(imageBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ImageIcon icon = new ImageIcon(Base64.getDecoder().decode(base64Image));
        albumCoverLabel.setIcon(icon);
    }

    public void setSongTitle(String title) {
        if (title == null) {
            title = "Placeholder Title";
        }
        songTitleLabel.setText(title);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                new RTPClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
