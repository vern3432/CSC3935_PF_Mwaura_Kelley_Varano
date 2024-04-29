package rtp;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.sound.sampled.*;

public class RTPClient {
    private JFrame frame;
    private JButton playButton;
    private boolean isPlaying = false;
    private SourceDataLine line;
    private DatagramSocket socket;
    private Thread receiveThread;
    private InetAddress serverAddress;
    private int serverPort = 25000;

    public RTPClient() throws LineUnavailableException, SocketException, UnknownHostException {
        socket = new DatagramSocket();
        serverAddress = InetAddress.getByName("localhost");
        initializeAudio();
        initializeGUI();
    }

    private void initializeGUI() {
        frame = new JFrame("RTP Audio Client");
        playButton = new JButton("Play");
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
        frame.getContentPane().add(playButton, BorderLayout.CENTER);
        frame.setSize(300, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void initializeAudio() throws LineUnavailableException {
        AudioFormat format = new AudioFormat(44100.0f, 16, 2, true, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format, 10000);
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
            try {
                byte[] buf = new byte[4096];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                line.start();
                while (isPlaying) {
                    socket.receive(packet);
                    line.write(packet.getData(), 12, packet.getLength() - 12);
                }
            } catch (IOException e) {
                System.out.println("Error in receiving packet: " + e.getMessage());
            }
        });
        receiveThread.start();
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
