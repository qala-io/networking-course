package io.qala.networking;

import io.qala.networking.ipv4.IpPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TrafficStats {
    private final HashMap<Nic, List<IpPacket>> ipPacketsReceived = new HashMap<>();
    private final HashMap<Nic, AtomicInteger> ipPacketsSent = new HashMap<>();

    public void receivedPacket(Nic nic, IpPacket packet) {
        ipPacketsReceived.computeIfAbsent(nic, (n) -> new ArrayList<>()).add(packet);
    }
    public List<IpPacket> getReceivedPackets(Nic nic) {
        if(!ipPacketsReceived.containsKey(nic))
            throw new IllegalArgumentException("This Kernel doesn't contain " + nic);
        return ipPacketsReceived.get(nic);
    }
    public void sentPacket(Nic nic) {
        ipPacketsSent.computeIfAbsent(nic, (n) -> new AtomicInteger()).incrementAndGet();
    }
    public int getSentPackets(Nic nic) {
        if(!ipPacketsSent.containsKey(nic))
            throw new IllegalArgumentException("This Kernel doesn't contain " + nic);
        return ipPacketsSent.get(nic).get();
    }
}
