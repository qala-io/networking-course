package io.qala.networking.ipv4;

import io.qala.networking.Bytes;
import io.qala.networking.l2.L2Packet;
import io.qala.networking.l2.Mac;

import java.util.Objects;

public class IpPacket {
    private static final byte TCP_CODE = 6;
    public static final Bytes GENERAL_HEADER = new Bytes(
            (4 << 4) | 1 /*IP version | IHL*/, 0, 0, 0,
            0, 0, 0, 0,
            64/*TTL*/, TCP_CODE, 0, 0// the last 2 bytes are are checksum for the header
    );
    private final Mac srcMac;
    private final IpAddress srcIp;
    private final Mac dstMac;
    private final IpAddress dstIp;
    private final Bytes payload;

    public IpPacket(Mac srcMac, IpAddress srcIp, Mac dstMac, IpAddress dstIp, Bytes payload) {
        this.srcMac = Objects.requireNonNull(srcMac);
        this.srcIp = Objects.requireNonNull(srcIp);
        this.dstMac = Objects.requireNonNull(dstMac);
        this.dstIp = Objects.requireNonNull(dstIp);
        this.payload = Objects.requireNonNull(payload);
    }
    public IpPacket(L2Packet l2) {
        this(
                l2.src(), new IpAddress(l2.getPayload().get(12, 12+4)),
                l2.dst(), new IpAddress(l2.getPayload().get(16, 16+4)),
                l2.getPayload().get(20, l2.getPayload().size()));
    }
    public static boolean isIp(L2Packet l2Packet) {
        Bytes payload = l2Packet.getPayload();
        return payload.startsWith(GENERAL_HEADER);
    }
    public IpAddress dst() {
        return dstIp;
    }
    public IpAddress src() {
        return srcIp;
    }
    public Mac dstMac() {
        return dstMac;
    }
    public Mac srcMac() {
        return srcMac;
    }
    public Bytes getPayload() {
        return payload;
    }
    public L2Packet toL2() {
        return new L2Packet(srcMac, dstMac, GENERAL_HEADER
                .append(srcIp.toBytes()).append(dstIp.toBytes()
                        .append(payload)));
    }
}
