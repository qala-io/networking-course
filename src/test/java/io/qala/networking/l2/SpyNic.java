package io.qala.networking.l2;

import io.qala.networking.Bytes;
import io.qala.networking.Link;
import io.qala.networking.Nic;
import io.qala.networking.ipv4.IpAddress;

import java.util.ArrayList;
import java.util.List;

public class SpyNic implements Nic {
    private final List<L2Packet> receivedPackets = new ArrayList<>();

    @Override public void receive(Link<L2Packet> src, L2Packet packet) {
        receivedPackets.add(packet);
    }
    @Override public void setEndpoint(Link<L2Packet> link) {

    }
    @Override public void sendArp(IpAddress dst) {

    }
    @Override public void send(IpAddress dstIp, Mac dstMac, Bytes body) {

    }
    public int size() {
        return receivedPackets.size();
    }
    public L2Packet get(int idx) {
        return receivedPackets.get(idx);
    }
}
