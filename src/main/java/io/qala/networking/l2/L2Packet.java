package io.qala.networking.l2;

import io.qala.networking.Bytes;

public class L2Packet {
    private final Mac src, dst;
    private final Bytes payload;

    public L2Packet(Mac src, Mac dst, Bytes payload) {
        this.src = src;
        this.dst = dst;
        this.payload = payload;
    }

    public Mac src() {
        return src;
    }
    public Mac dst() {
        return dst;
    }
    public Bytes toBytes() {
        return src.toBytes().append(dst.toBytes()).append(payload);
    }
    public Bytes getPayload() {
        return payload;
    }
}
