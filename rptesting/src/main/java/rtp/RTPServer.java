package rtp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
public class RTPServer {
    private int port;
    private DatagramSocket socket;
    private AudioFileLoader audioLoader;
    private AudioFileProperties fileProperties;

    public RTPServer(int port, String audioFilePath) throws Exception {
        this.port = port;
        this.audioLoader = new AudioFileLoader(audioFilePath);
        this.fileProperties = new AudioFileProperties(audioFilePath);
        this.socket = new DatagramSocket();
    }

    public void startStreaming(InetAddress clientAddress) {
        try {
            byte[] audioData = audioLoader.loadAudioFile();
            int sequenceNumber = 0;
            long timestamp = 0;

            int frameSize = fileProperties.getFrameSize();
            float frameRate = fileProperties.getFrameRate();
            int packetSize = frameSize * 40; // E.g., 40 frames per packet

            for (int i = 0; i < audioData.length; i += packetSize) {
                int len = Math.min(packetSize, audioData.length - i);
                byte[] packetData = new byte[len];
                System.arraycopy(audioData, i, packetData, 0, len);
                RTPpacket rtpPacket = new RTPpacket(10, sequenceNumber++, timestamp, packetData);
                byte[] packetBytes = rtpPacket.getPacketBytes();

                DatagramPacket packet = new DatagramPacket(packetBytes, packetBytes.length, clientAddress, port);
                socket.send(packet);

                int samplesPerPacket = len / frameSize+100;
                long sleepMillis = (long) (1000 * (samplesPerPacket / frameRate));
                Thread.sleep(sleepMillis);
                timestamp += samplesPerPacket;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (socket != null) {
            socket.close();
        }
    }
}
