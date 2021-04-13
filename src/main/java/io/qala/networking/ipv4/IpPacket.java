package io.qala.networking.ipv4;

import io.qala.networking.Bytes;
import io.qala.networking.l2.L2Packet;
import io.qala.networking.l2.Mac;

public class IpPacket {
    private final Mac srcMac;
    private final IpAddress srcIp;
    private final Mac dstMac;
    private final IpAddress dstIp;
    private final Bytes payload;

    public IpPacket(Mac srcMac, IpAddress srcIp, Mac dstMac, IpAddress dstIp, Bytes payload) {
        this.srcMac = srcMac;
        this.srcIp = srcIp;
        this.dstMac = dstMac;
        this.dstIp = dstIp;
        this.payload = payload;
    }
    public IpPacket(L2Packet l2Packet) {
        this(null, null, null, null, null);
    }
    public static boolean isIp(L2Packet l2Packet) {
        return false;
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
    public Bytes getPayload() {
        return payload;
    }
    public L2Packet toL2() {
        return null;
    }
}
