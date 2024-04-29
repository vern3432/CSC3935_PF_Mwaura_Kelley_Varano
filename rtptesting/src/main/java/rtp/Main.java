package rtp;


import java.net.InetAddress;

public class Main {
    public static void main(String[] args) {
        try {
            RTPServer server = new RTPServer(5004, "/home/linxuser3/Documents/CSC3935_PF_Mwaura_Kelley_Varano/SampleAudio/Sample.wav");
            InetAddress clientAddress = InetAddress.getByName("127.0.0.1");
            server.startStreaming(clientAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
