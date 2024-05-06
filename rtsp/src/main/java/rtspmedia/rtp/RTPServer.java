package rtspmedia.rtp;

import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

import merrimackutil.json.JsonIO;
import merrimackutil.json.types.JSONObject;

public class RTPServer {
    public int RTP_PORT;
    private DatagramSocket socket;
    private static String configFile = "data/rtp-server-config/config.json";
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

    public void setAudioFile(File audioFile) {
        this.audioFile = audioFile;
    }

    public RTPServer() throws SocketException {
        try {
            JSONObject configObj = JsonIO.readObject(new File(configFile));
            config = new RTPServerConfiguration(configObj);
            this.RTP_PORT = config.getPort();
        } catch (FileNotFoundException e) {
            System.out.println("Could not find server configuration file.");
            e.printStackTrace();
        } catch (InvalidObjectException e) {
            System.out.println("Invalid json object for Server configuration");
            e.printStackTrace();
        }
        this.socket = new DatagramSocket(RTP_PORT);
        audioFile = new File("SampleAudio/YoullFindaWay.wav");

    }

    public RTPServer(String filepathString) throws SocketException {
        try {
            JSONObject configObj = JsonIO.readObject(new File(configFile));
            config = new RTPServerConfiguration(configObj);
            this.RTP_PORT = config.getPort();
        } catch (FileNotFoundException e) {
            System.out.println("Could not find server configuration file.");
            e.printStackTrace();
        } catch (InvalidObjectException e) {
            System.out.println("Invalid json object for Server configuration");
            e.printStackTrace();
        }
        this.audioFile = new File(filepathString);
        this.socket = new DatagramSocket(RTP_PORT);

    }

    public int getRTP_PORT() {
        return RTP_PORT;
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
            socket.receive(packet); // Receive initial connection request
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