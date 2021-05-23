package io.qala.networking;

import io.qala.networking.ipv4.IpPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TrafficStats {
    private final HashMap<NetDevice, List<IpPacket>> ipPacketsReceived = new HashMap<>();
    private final HashMap<NetDevice, AtomicInteger> ipPacketsSent = new HashMap<>();

    public void receivedPacket(NetDevice dev, IpPacket packet) {
        ipPacketsReceived.computeIfAbsent(dev, (n) -> new ArrayList<>()).add(packet);
    }
    public List<IpPacket> getReceivedPackets(NetDevice dev) {
        if(!ipPacketsReceived.containsKey(dev))
            throw new IllegalArgumentException("This Kernel doesn't contain " + dev);
        return ipPacketsReceived.get(dev);
    }
    public void sentPacket(NetDevice dev) {
        ipPacketsSent.computeIfAbsent(dev, (n) -> new AtomicInteger()).incrementAndGet();
    }
    public int getSentPackets(NetDevice dev) {
        if(!ipPacketsSent.containsKey(dev))
            throw new IllegalArgumentException("This Kernel doesn't contain " + dev);
        return ipPacketsSent.get(dev).get();
    }
}
