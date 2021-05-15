package io.qala.networking.ipv4;

import io.qala.networking.Bytes;
import io.qala.networking.l2.L2Packet;
import io.qala.networking.l2.Mac;

import java.util.Objects;

public class ArpPacket {
    private static final int MAC_BYTES = 6, IPV4_BYTES = 4;
    private final Mac src, dst;
    private final IpAddress srcIp, dstIp;
    private final Type type;
    private static final Bytes PACKET_HEADER = new Bytes(
            0, 1, //hardware type: Ethernet
            8, 0, // protocol: IPv4
            MAC_BYTES,// hardware size aka local protocol address length
            IPV4_BYTES,//protocol size aka global protocol address length
            0
    );

    public ArpPacket(Type type, Mac src, Mac dst, IpAddress srcIp, IpAddress dstIp) {
        this.src = Objects.requireNonNull(src);
        this.dst = Objects.requireNonNull(dst);
        this.srcIp = Objects.requireNonNull(srcIp);
        this.dstIp = Objects.requireNonNull(dstIp);
        this.type = Objects.requireNonNull(type);
    }
    public ArpPacket(L2Packet l2Packet) {
        this.src = l2Packet.src();
        this.dst = l2Packet.dst();
        int offset = PACKET_HEADER.size();
        this.type = Type.parseCode(l2Packet.getPayload().get(offset++));
        offset += MAC_BYTES;// src MAC
        this.srcIp = new IpAddress(l2Packet.getPayload().get(offset, offset+=IPV4_BYTES));
        this.dstIp = new IpAddress(l2Packet.getPayload().get(offset+=MAC_BYTES, offset+IPV4_BYTES));
    }
    public static ArpPacket req(Mac src, IpAddress srcIp, Mac dst, IpAddress dstIp) {
        return new ArpPacket(Type.REQUEST, src, dst, srcIp, dstIp);
    }
    public Mac srcMac() {
        return src;
    }
    public Mac dstMac() {
        return dst;
    }
    public IpAddress dstIp() {
        return dstIp;
    }
    public IpAddress srcIp() {
        return srcIp;
    }
    public L2Packet toL2() {
        return new L2Packet(src, dst, toL2Payload());
    }

    public static boolean isArp(L2Packet l2Packet) {
        return l2Packet.getPayload().startsWith(PACKET_HEADER);
    }
    public boolean isReply() {
        return type == Type.REPLY;
    }
    public boolean isReq() {
        return type == Type.REQUEST;
    }

    private Bytes toL2Payload() {
        return PACKET_HEADER.append(type.operationCode)
                .append(src.toBytes())
                .append(srcIp.toBytes())// interestingly we duplicate src MAC in L2 header too
                .append(Mac.EMPTY.toBytes())// target MAC will already be present in L2 header
                .append(dstIp.toBytes());
    }
    public ArpPacket createReply(Mac src) {
        if(!isReq())
            throw new IllegalStateException("This wasn't a request in the first place");
        return new ArpPacket(Type.REPLY, src, this.src, this.dstIp, this.srcIp);
    }

    public enum Type {
        REQUEST(1), REPLY(2);
        private final byte operationCode;

        Type(int operationCode) {
            this.operationCode = (byte) operationCode;
        }
        static Type parseCode(byte code) {
            for (Type next : values())
                if (next.operationCode == code)
                    return next;
            throw new IllegalArgumentException("" + code);
        }
    }
}
