package rtp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class RTPClient {
    private DatagramSocket socket;
    private int port;

    public RTPClient(int port) throws SocketException {
        this.port = port;
        this.socket = new DatagramSocket(port);
    }

    public byte[] receivePacket() throws Exception {
        byte[] buffer = new byte[2048]; // Large enough to receive RTP packets
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return packet.getData();
    }

    public void close() {
        if (socket != null) {
            socket.close();
        }
    }
}
