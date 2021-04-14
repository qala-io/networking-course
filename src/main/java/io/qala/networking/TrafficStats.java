package io.qala.networking;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TrafficStats {
    private final HashMap<Nic, AtomicInteger> ipPacketsReceived = new HashMap<>();
    private final HashMap<Nic, AtomicInteger> ipPacketsSent = new HashMap<>();

    public void receivedPacket(Nic nic) {
        ipPacketsReceived.computeIfAbsent(nic, (n) -> new AtomicInteger()).incrementAndGet();
    }
    public void sentPacket(Nic nic) {
        ipPacketsSent.computeIfAbsent(nic, (n) -> new AtomicInteger()).incrementAndGet();
    }
    public int getReceivedPackets(Nic nic) {
        return ipPacketsReceived.get(nic).get();
    }
    public int getSentPackets(Nic nic) {
        return ipPacketsSent.get(nic).get();
    }
}
