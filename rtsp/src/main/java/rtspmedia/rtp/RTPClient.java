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
import javax.sound.sampled.*;

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

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    private long currentPlaybackTime = 0; // Tracks the current playback time in milliseconds

    public RTPClient() throws LineUnavailableException, SocketException, UnknownHostException {
        socket = new DatagramSocket();
        serverAddress = InetAddress.getByName("localhost");
        initializeAudio();
        initializeGUI();
        setAlbumCover(null); // Set default album cover
        setSongTitle(null); // Set default song title
    }

    public RTPClient(int port ) throws LineUnavailableException, SocketException, UnknownHostException {
        this.serverPort=port;
        socket = new DatagramSocket();
        serverAddress = InetAddress.getByName("localhost");
        initializeAudio();
        initializeGUI();
        setAlbumCover(null); // Set default album cover
        setSongTitle(null); // Set default song title
    }

    public RTPClient(int port,long length ) throws LineUnavailableException, SocketException, UnknownHostException {
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

    private void initializeGUI() {
        frame = new JFrame("RTP Audio Client");
        playButton = new JButton("Play");
        progressBar = new JProgressBar(0, 100); // Max 100 for percentage
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        frame.getContentPane().add(albumCoverLabel, BorderLayout.WEST);
        frame.getContentPane().add(songTitleLabel, BorderLayout.SOUTH);

        playButton.addActionListener(e -> {
            if (isPlaying) {
                stop();
                playButton.setText("Play");
            } else {
                try {
                    sendConnectionRequest();
                    playButton.setText("Pause");
                } catch (IOException ex) {
                    System.out.println("Error sending connection request: " + ex.getMessage());
                }
            }
            isPlaying = !isPlaying;
        });

        frame.getContentPane().add(playButton, BorderLayout.NORTH);
        frame.getContentPane().add(progressBar, BorderLayout.CENTER);
        frame.setSize(400, 400); // Set the frame size to three times the length and twice the width
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    
    /** 
     * @throws LineUnavailableException
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
        byte[] message = "Hello, Server!".getBytes();
        DatagramPacket packet = new DatagramPacket(message, message.length, serverAddress, serverPort);
        socket.send(packet);
        if (!isPlaying) {
            startReceiving();
        }
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
                    line.write(audioData, 0, audioData.length); // Play audio data
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
        updateProgress(currentPlaybackTime);
    }

    private void updateProgress(long playbackTime) {
        int progress = (int)((playbackTime * 100) / totalDuration); // Calculate progress percentage
        progressBar.setValue(progress*2);
    }

    private void stop() {
        isPlaying = false;
        line.stop();
        line.flush();
        if (receiveThread != null) {
            receiveThread.interrupt();
        }
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