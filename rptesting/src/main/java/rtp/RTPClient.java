package rtp;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.*;
import javax.sound.sampled.*;

public class RTPClient {
    private JFrame frame;
    private JButton playButton;
    private JProgressBar progressBar;
    private boolean isPlaying = false;
    private SourceDataLine line;
    private DatagramSocket socket;
    private Thread receiveThread;
    private InetAddress serverAddress;
    private int serverPort = 25000;
    private long totalDuration = 300000; // Example total duration in milliseconds for a 5-minute audio
    private BlockingQueue<byte[]> audioQueue = new LinkedBlockingQueue<>();

    public RTPClient() throws LineUnavailableException, SocketException, UnknownHostException {
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
        frame.setSize(300, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

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
                    audioQueue.put(Arrays.copyOfRange(packet.getData(), 12, packet.getLength())); // Store data in buffer
                    processAudioQueue(); // Manage audio playback from queue
                } catch (IOException | InterruptedException e) {
                    System.out.println("Error in receiving packet: " + e.getMessage());
                }
            }
        });
        receiveThread.start();
    }

    private void processAudioQueue() throws InterruptedException {
        while (isPlaying && !audioQueue.isEmpty()) {
            byte[] audioData = audioQueue.take();
            line.write(audioData, 0, audioData.length);
            // Optional: Update progress here based on audio playback time
        }
    }

    private void updateProgress(long timestamp) {
        int progress = (int)((timestamp * 100) / totalDuration); // Calculate progress percentage
        progressBar.setValue(progress);
    }

    private void stop() {
        isPlaying = false;
        line.stop();
        line.flush();
        if (receiveThread != null) {
            receiveThread.interrupt();
        }
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
