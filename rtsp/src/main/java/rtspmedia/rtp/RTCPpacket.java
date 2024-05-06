package rtspmedia.rtp;

import java.nio.ByteBuffer;

public class RTCPpacket {
    final static int HEADER_SIZE = 8;
    final static int BODY_SIZE = 24;

    public int V;
    public int P;
    public int RC;
    public int payloadType;
    public int length;
    public int SSRC;
    public float fractionLost;
    public int cumulativeLost;
    public int highestSequenceNumber;
    public int interarrivalJitter;
    public int LSR;
    public int DLSR;
    public byte[] header;
    public byte[] body;

    public RTCPpacket(float fractionLost, int cumulativeLost, int highestSequenceNumber) {
        V = 2;
        P = 0;
        RC = 1;
        payloadType = 201;
        length = 32;

        this.fractionLost = fractionLost;
        this.cumulativeLost = cumulativeLost;
        this.highestSequenceNumber = highestSequenceNumber;

        header = new byte[HEADER_SIZE];
        body = new byte[BODY_SIZE];

        header[0] = (byte) (V << 6 | P << 5 | RC);
        header[1] = (byte) (payloadType & 0xFF);
        header[2] = (byte) (length >> 8);
        header[3] = (byte) (length & 0xFF);
        header[4] = (byte) (SSRC >> 24);
        header[5] = (byte) (SSRC >> 16);
        header[6] = (byte) (SSRC >> 8);
        header[7] = (byte) (SSRC & 0xFF);

        ByteBuffer bb = ByteBuffer.wrap(body);
        bb.putFloat(fractionLost);
        bb.putInt(cumulativeLost);
        bb.putInt(highestSequenceNumber);
    }

    public RTCPpacket(byte[] packet, int packet_size) {

        header = new byte[HEADER_SIZE];
        body = new byte[BODY_SIZE];

        System.arraycopy(packet, 0, header, 0, HEADER_SIZE);
        System.arraycopy(packet, HEADER_SIZE, body, 0, BODY_SIZE);

        V = (header[0] & 0xFF) >> 6;
        payloadType = header[1] & 0xFF;
        length = (header[3] & 0xFF) + ((header[2] & 0xFF) << 8);
        SSRC = (header[7] & 0xFF) + ((header[6] & 0xFF) << 8) + ((header[5] & 0xFF) << 16) + ((header[4] & 0xFF) << 24);

        ByteBuffer bb = ByteBuffer.wrap(body);
        fractionLost = bb.getFloat();
        cumulativeLost = bb.getInt();
        highestSequenceNumber = bb.getInt();
    }

    public int getpacket(byte[] packet) {
        System.arraycopy(header, 0, packet, 0, HEADER_SIZE);
        System.arraycopy(body, 0, packet, HEADER_SIZE, BODY_SIZE);
        return (BODY_SIZE + HEADER_SIZE);
    }

    public int getlength() {
        return (BODY_SIZE + HEADER_SIZE);
    }

    public String toString() {
        return "[RTCP] V: " + V + ", Fraction Lost: " + fractionLost
                + ", Cumulative Lost: " + cumulativeLost + ", Highest Seq Num: " + highestSequenceNumber;
    }
}
