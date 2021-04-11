package io.qala.networking.l2;

import io.qala.networking.L1Endpoint;

import java.util.ArrayList;
import java.util.List;

public class SpyEndpoint implements L1Endpoint {
    private final List<L2Packet> receivedPackets = new ArrayList<>();

    @Override public void receive(L2Packet packet) {
        receivedPackets.add(packet);
    }
    public int size() {
        return receivedPackets.size();
    }
    public L2Packet get(int idx) {
        return receivedPackets.get(idx);
    }
}
