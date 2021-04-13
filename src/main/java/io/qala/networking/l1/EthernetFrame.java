package io.qala.networking.l1;

import io.qala.networking.l2.Mac;

public class EthernetFrame {
    private final Mac src, dst;
    private final Byte payload;

    public EthernetFrame(Mac src, Mac dst, Byte payload) {
        this.src = src;
        this.dst = dst;
        this.payload = payload;
    }


}
