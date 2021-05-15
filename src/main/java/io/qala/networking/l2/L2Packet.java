package io.qala.networking.l2;

import io.qala.networking.Bytes;
import io.qala.networking.NetDevice;

public class L2Packet {
    private final Mac src, dst;
    private final Bytes payload;
    /**
     * In case of incoming request this is the device which received the packet.
     */
    private NetDevice dev;
    private Boolean toUs;

    public L2Packet(Mac src, Mac dst, Bytes payload) {
        this.src = src;
        this.dst = dst;
        this.payload = payload;
    }
    public L2Packet(Bytes packet, NetDevice dev) {
        this.dev = dev;
        this.src = new Mac(packet.get(0, 6));
        this.dst = new Mac(packet.get(6, 12));
        this.payload = packet.get(12, packet.size());
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
    public void setToUs(boolean toUs) {
        this.toUs = toUs;
    }
    public boolean isToUs() {
        return toUs;
    }

    public NetDevice getDev() {
        return dev;
    }
}
