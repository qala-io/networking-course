package io.qala.networking.ipv4;

import io.qala.networking.Bytes;
import io.qala.networking.l2.L2Packet;
import io.qala.networking.l2.Mac;

public class ArpPacket {
    private final Mac src, dst;
    private final IpAddress srcIp, dstIp;
    private final Type type;
    private static final Bytes PACKET_HEADER = new Bytes(
            0, 1, //hardware type: Ethernet
            8, 0, // protocol: IPv4
            6,// hardware size
            4, //protocol size
            0
    );

    public ArpPacket(Type type, Mac src, Mac dst, IpAddress srcIp, IpAddress dstIp) {
        this.src = src;
        this.dst = dst;
        this.srcIp = srcIp;
        this.dstIp = dstIp;
        this.type = type;
    }
    public ArpPacket(L2Packet l2Packet) {
        this.src = l2Packet.src();
        this.dst = l2Packet.dst();
        int offset = PACKET_HEADER.size();
        this.type = Type.parseCode(l2Packet.getPayload().get(offset++));
        offset+=6;// src MAC
        this.srcIp = new IpAddress(l2Packet.getPayload().get(offset, offset+=4));
        this.dstIp = new IpAddress(l2Packet.getPayload().get(offset+=6, offset+4));
    }
    public static ArpPacket req(Mac src, Mac dst, IpAddress srcIp, IpAddress dstIp) {
        return new ArpPacket(Type.REQUEST, src, dst, srcIp, dstIp);
    }

    public L2Packet toL2() {
        return new L2Packet(src, dst, toL2Payload());
    }

    public static boolean isArpReq(L2Packet l2Packet) {
        return l2Packet.getPayload().startsWith(PACKET_HEADER.append(Type.REQUEST.operationCode));
    }

    private Bytes toL2Payload() {
        return PACKET_HEADER.append(type.operationCode)
                .append(src.toBytes())
                .append(srcIp.toBytes())// for some reason we duplicate target MAC in L2 header and here
                .append(Mac.EMPTY.toBytes())// target MAC will already be present in L2 header
                .append(dstIp.toBytes());
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
