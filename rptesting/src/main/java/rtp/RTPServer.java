package rtp;

import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

public class RTPServer {
    public static final int RTP_PORT = 25000;
    private DatagramSocket socket;
    private InetAddress clientIP;
    private int clientPort;
    private AudioInputStream audioStream;
    private byte[] buffer = new byte[4096];

    public RTPServer() throws SocketException {
        this.socket = new DatagramSocket(RTP_PORT);
    }

    public void start() {
        System.out.println("Server is running and waiting for connection...");
        receiveClientConnection();
        processAudioStream();
    }

    private void receiveClientConnection() {
        try {
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);  // Receive initial connection request
            String received = new String(packet.getData(), 0, packet.getLength());
            if (received.trim().equals("Hello, Server!")) {
                clientIP = packet.getAddress();
                clientPort = packet.getPort();
                System.out.println("Client connected: " + clientIP + ":" + clientPort);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processAudioStream() {
        try {
            File audioFile = new File("/home/linxuser3/Documents/CSC3935_PF_Mwaura_Kelley_Varano/SampleAudio/Sample.wav");
            audioStream = AudioSystem.getAudioInputStream(audioFile);
            int readBytes;
            int sequenceNumber = 0;
            long timestamp = 0;

            AudioFormat format = audioStream.getFormat();
            long frameSize = format.getFrameSize();
            float frameRate = format.getFrameRate();
            int bufferSize = 512;
            buffer = new byte[bufferSize];

            while ((readBytes = audioStream.read(buffer)) != -1) {
                RTPpacket rtpPacket = new RTPpacket(10, sequenceNumber++, timestamp, buffer);
                byte[] packetBytes = rtpPacket.getPacketBytes();
                DatagramPacket sendPacket = new DatagramPacket(packetBytes, packetBytes.length, clientIP, clientPort);
                socket.send(sendPacket);
                timestamp += (bufferSize / frameSize) * (1000 / (long) frameRate);
                Thread.sleep((bufferSize / frameSize) * (1000 / (long) frameRate));
            }
            System.out.println("Audio streaming complete.");
        } catch (UnsupportedAudioFileException | IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (audioStream != null) {
                    audioStream.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            RTPServer server = new RTPServer();
            server.start();
        } catch (SocketException e) {
            System.out.println("Failed to start the server: " + e.getMessage());
        }
    }
}
