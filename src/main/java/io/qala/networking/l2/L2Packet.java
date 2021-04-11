package io.qala.networking.l2;

public class L2Packet {
    private final Mac src, dst;
    private final byte[] data;

    public L2Packet(Mac src, Mac dst, byte[] data) {
        this.src = src;
        this.dst = dst;
        this.data = data;
    }
    public Mac getSrc() {
        return src;
    }
    public Mac getDst() {
        return dst;
    }
}
