package io.qala.networking.ipv4;

import io.qala.networking.Bytes;
import io.qala.networking.l2.L2Packet;
import io.qala.networking.l2.Mac;

public class IpPacket {
    private static final byte TCP_CODE = 6;
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
    public IpPacket(L2Packet l2) {
        this(
                l2.src(), new IpAddress(l2.getPayload().get(12, 12+4)),
                l2.dst(), new IpAddress(l2.getPayload().get(16, 16+4)),
                l2.getPayload().get(20, l2.getPayload().size()));
    }
    public static boolean isIp(L2Packet l2Packet) {
        throw new UnsupportedOperationException();
    }
    public IpAddress dst() {
        return null;
    }
    public IpAddress src() {
        return null;
    }
    public Bytes getPayload() {
        return payload;
    }
    public L2Packet toL2() {
        Bytes header = new Bytes(
                (4 << 4) | 1 /*IP version | IHL*/, 0, 0, 0,
                0, 0, 0, 0,
                64/*TTL*/, TCP_CODE, 0, 0// the last 2 bytes are are checksum for the header
        ).append(srcIp.toBytes()).append(dstIp.toBytes());
        return new L2Packet(srcMac, dstMac, header.append(payload));
    }
}
