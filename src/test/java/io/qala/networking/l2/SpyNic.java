package io.qala.networking.l2;

import io.qala.networking.Endpoint;
import io.qala.networking.Nic;

import java.util.ArrayList;
import java.util.List;

public class SpyNic implements Nic {
    private final List<L2Packet> receivedPackets = new ArrayList<>();

    @Override public void process(Endpoint<L2Packet> src, L2Packet packet) {
        receivedPackets.add(packet);
    }
    @Override public void setEndpoint(Endpoint<L2Packet> endpoint) {

    }
    public int size() {
        return receivedPackets.size();
    }
    public L2Packet get(int idx) {
        return receivedPackets.get(idx);
    }
}
