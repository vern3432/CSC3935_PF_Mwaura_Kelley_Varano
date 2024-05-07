package rtspmedia.rtp;

import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

import merrimackutil.json.JsonIO;
import merrimackutil.json.types.JSONObject;

/**
 * The RTPServer class handles the setup and streaming of audio data over RTP to a client.
 */
public class RTPServer {
    public int RTP_PORT;
    private DatagramSocket socket;
    private static RTPServerConfiguration config;
    private InetAddress clientIP;
    private int clientPort;
    private AudioInputStream audioStream;
    private byte[] buffer = new byte[4096];
    File audioFile;

    /**
     * @return File
     */
    public File getAudioFile() {
        return audioFile;
    }

    
    /** 
     * @param audioFile
     */
    public void setAudioFile(File audioFile) {
        this.audioFile = audioFile;
    }

    /**
     * Default constructor that initializes the server with a default audio file.
     * @throws SocketException if there is an error creating the socket.
     */
    public RTPServer() throws SocketException {
        this.RTP_PORT = setAvailablePort();
        this.socket = new DatagramSocket(RTP_PORT);
        audioFile = new File("SampleAudio/YoullFindaWay.wav");

    }

    /**
     * Constructor that initializes the server with a specified audio file path.
     * @param filepathString The path to the audio file to be streamed.
     * @throws SocketException if there is an error creating the socket.
     */
    public RTPServer(String filepathString) throws SocketException {
        this.RTP_PORT = setAvailablePort();
        this.audioFile = new File(filepathString);
        this.socket = new DatagramSocket(RTP_PORT);

    }

    /**
     * Retrieves the RTP port number.
     * @return the RTP port number.
     */
    public int getRTP_PORT() {
        return RTP_PORT;
    }

    /**
     * Starts the server, waits for client connection, and processes the audio stream.
     */
    public void start() {
        System.out.println("Server is running and waiting for connection...");
        receiveClientConnection();
        processAudioStream();
    }

    private void receiveClientConnection() {
        while (true) { // Continuously listen for new connections
            try {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                if (received.trim().equals("Hello, Server!")) {
                    clientIP = packet.getAddress();
                    clientPort = packet.getPort();
                    System.out.println("Client connected: " + clientIP + ":" + clientPort);
                    processAudioStream(); // Start or resume sending the audio stream
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (socket.isClosed()) {
                    break; // Break if the socket is closed, possibly restart or handle error
                }
            }
        }
    }
    

    private void processAudioStream() {
        try {
            audioStream = AudioSystem.getAudioInputStream(audioFile); // Load the audio file into the stream
            int readBytes;
            int sequenceNumber = 0;
            long timestamp = 0;

            AudioFormat format = audioStream.getFormat();
            long frameSize = format.getFrameSize();
            float frameRate = format.getFrameRate();
            int bufferSize = 512;
            buffer = new byte[bufferSize];

            while ((readBytes = audioStream.read(buffer)) != -1) { // Continue reading until end of stream
                RTPpacket rtpPacket = new RTPpacket(10, sequenceNumber++, timestamp, buffer);
                byte[] packetBytes = rtpPacket.getPacketBytes();
                DatagramPacket sendPacket = new DatagramPacket(packetBytes, packetBytes.length, clientIP, clientPort);
                socket.send(sendPacket); // Send the RTP packet to the client

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

    /**
     * Sets an available port for the RTP server.
     * @return the available port number or -1 if no port could be found.
     */
    public int setAvailablePort() {
        ServerSocket serverSocket = null;
        try {
            // Create a new ServerSocket and get its port
            serverSocket = new ServerSocket(0);
            int port = serverSocket.getLocalPort();
            // Close the ServerSocket
            serverSocket.close();
            // Set the RTP_PORT to the found port
            this.RTP_PORT = port;
            return port;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1; // Return -1 if no port could be found
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
