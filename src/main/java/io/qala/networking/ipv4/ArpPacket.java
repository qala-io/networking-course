package io.qala.networking.ipv4;

import io.qala.networking.Bytes;
import io.qala.networking.l2.L2Packet;
import io.qala.networking.l2.Mac;

public class ArpPacket {
    private final Mac src, dst;
    private final IpAddress srcIp, dstIp;
    private final Type type;

    public ArpPacket(Type type, Mac src, Mac dst, IpAddress srcIp, IpAddress dstIp) {
        this.src = src;
        this.dst = dst;
        this.srcIp = srcIp;
        this.dstIp = dstIp;
        this.type = type;
    }
    public static ArpPacket req(Mac src, Mac dst, IpAddress srcIp, IpAddress dstIp) {
        return new ArpPacket(Type.REQUEST, src, dst, srcIp, dstIp);
    }

    public L2Packet toL2() {
        return new L2Packet(src, dst, toL2Payload());
    }
    private Bytes toL2Payload() {
        Bytes bytes = new Bytes(
                0, 1, //hardware type: Ethernet
                8, 0, // protocol: IPv4
                6,// hardware size
                4, //protocol size
                0, type.operationCode
        );
        return bytes.append(src.toBytes())
                .append(srcIp.toBytes())// for some reason we duplicate target MAC in L2 header and here
                .append(Mac.EMPTY.toBytes())// target MAC will already be present in L2 header
                .append(dstIp.toBytes());
    }

    private enum Type {
        REQUEST(1), REPLY(2);
        private final byte operationCode;

        Type(int operationCode) {
            this.operationCode = (byte) operationCode;
        }
    }
}
