package io.qala.networking.l2;

import io.qala.networking.Bytes;

public class L2Packet {
    private final Mac src, dst;
    private final Bytes data;

    public L2Packet(Mac src, Mac dst, Bytes data) {
        this.src = src;
        this.dst = dst;
        this.data = data;
    }

    public Mac src() {
        return src;
    }
    public Mac dst() {
        return dst;
    }
    public Bytes toBytes() {
        return src.toBytes().append(dst.toBytes()).append(data);
    }
}
