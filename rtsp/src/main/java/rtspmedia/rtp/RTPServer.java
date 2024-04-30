package rtspmedia.rtp;

import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

public class RTPServer {
    public static final int RTP_PORT = 25000;
    private static RTPServer instance;
    private DatagramSocket socket;
    private InetAddress clientIP;
    private int clientPort;
    private AudioInputStream audioStream;
    private byte[] buffer = new byte[4096];
    File audioFile;

    public RTPServer() throws SocketException {
        if (this.socket == null || this.socket.isClosed()) {
            this.socket = new DatagramSocket(RTP_PORT); // Use a fixed port
        }
        System.out.println("RTP Server is running on port: " + socket.getLocalPort());
        audioFile = new File("SampleAudio/YoullFindaWay.wav");
    }

    public static RTPServer getInstance() {
        if (instance == null) {
            try {
                instance = new RTPServer();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    /** 
     * @return File
     */
    public File getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(File audioFile) {
        this.audioFile = audioFile;
    }

    public void start() {

        System.out.println("Server is running and waiting for connection...");
        receiveClientConnection();
        processAudioStream();
    }
    public int getSocket(){
        System.out.println("Getting Socket:"+socket.getLocalPort());
        return socket.getLocalPort();
    }

    private void receiveClientConnection() {
        try {
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet); // Receive initial connection request
            String received = new String(packet.getData(), 0, packet.getLength());
            if (received.trim().equals("Hello, Server!")) {
                clientIP = packet.getAddress();
                clientPort = packet.getPort();
                System.out.println("Client connected: " + clientIP + ":" + clientPort);

                // Listen for song path
                ByteArrayInputStream byteStream = new ByteArrayInputStream(packet.getData());
                ObjectInputStream input = new ObjectInputStream(byteStream);
                String songPath = (String) input.readObject();
                RTPServer rtpServer = RTPServer.getInstance();
                System.out.println("Playing song: " + songPath);
                rtpServer.setAudioFile(new File(songPath));
                rtpServer.start();

                
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void processAudioStream() {
        try {
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
                // Example to adjust timing based on buffer length and audio format
                long frameDuration = (buffer.length / frameSize) * 1000 / (long) frameRate; // Duration of each frame in
                                                                                            // milliseconds
                Thread.sleep(frameDuration); // Sleep to maintain real-time streaming

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
            RTPServer server = RTPServer.getInstance();
            server.start();
        } catch (Exception e) {
            System.out.println("Failed to start the server: " + e.getMessage());
        }
    }
}