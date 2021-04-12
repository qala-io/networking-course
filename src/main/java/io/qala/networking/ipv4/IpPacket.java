package io.qala.networking.ipv4;

import io.qala.networking.l2.L2Packet;

public class IpPacket {
    private final L2Packet l2Packet;

    public IpPacket(L2Packet l2Packet) {
        this.l2Packet = l2Packet;
    }
    public IpAddress dst() {
        return null;
    }
    public IpAddress src() {
        return null;
    }
    public boolean isArp() {
        throw new UnsupportedOperationException();
    }
}
