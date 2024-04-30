package rtspmedia.rtp;

public class RTPpacket {
    public int version;
    public int padding;
    public int extension;
    public int csrcCount;
    public int marker;
    public int payloadType;
    public int sequenceNumber;
    public long timestamp;
    public int ssrc;
    public byte[] payloadData;

    public RTPpacket(int payloadType, int sequenceNumber, long timestamp, byte[] data) {
        this.version = 2;  // RTP version 2
        this.padding = 0;
        this.extension = 0;
        this.marker = 0;
        this.csrcCount = 0;
        this.payloadType = payloadType;
        this.sequenceNumber = sequenceNumber;
        this.timestamp = timestamp;
        this.ssrc = (int) (Math.random() * Integer.MAX_VALUE);
        this.payloadData = data;
    }

    // Method to get the byte array of the RTP packet
    public byte[] getPacketBytes() {
        // Calculate packet length
        int headerLength = 12; // Fixed header length
        int totalLength = headerLength + payloadData.length;

        byte[] packetBytes = new byte[totalLength];
        // Build the RTP header
        packetBytes[0] = (byte) (version << 6 | padding << 5 | extension << 4 | csrcCount);
        packetBytes[1] = (byte) (marker << 7 | payloadType & 0x7F);
        packetBytes[2] = (byte) (sequenceNumber >> 8);
        packetBytes[3] = (byte) (sequenceNumber & 0xFF);
        packetBytes[4] = (byte) (timestamp >> 24);
        packetBytes[5] = (byte) (timestamp >> 16);
        packetBytes[6] = (byte) (timestamp >> 8);
        packetBytes[7] = (byte) (timestamp & 0xFF);
        packetBytes[8] = (byte) (ssrc >> 24);
        packetBytes[9] = (byte) (ssrc >> 16);
        packetBytes[10] = (byte) (ssrc >> 8);
        packetBytes[11] = (byte) (ssrc & 0xFF);

        // Copy payload data into packetBytes
        System.arraycopy(payloadData, 0, packetBytes, headerLength, payloadData.length);

        return packetBytes;
    }
}
