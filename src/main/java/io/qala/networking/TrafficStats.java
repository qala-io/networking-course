package io.qala.networking;

import io.qala.networking.ipv4.IpPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TrafficStats {
    private final HashMap<NetDevice, List<IpPacket>> ipPacketsReceived = new HashMap<>();
    private final HashMap<NetDevice, AtomicInteger> ipPacketsSent = new HashMap<>();
    private final HashMap<NetDevice, List<IpPacket>> ipPacketsForwarded = new HashMap<>();

    public void receivedPacket(NetDevice dev, IpPacket packet) {
        ipPacketsReceived.computeIfAbsent(dev, (n) -> new ArrayList<>()).add(packet);
    }
    public List<IpPacket> getReceivedPackets(NetDevice dev) {
        return ipPacketsReceived.getOrDefault(dev, new ArrayList<>());
    }
    public void sentPacket(NetDevice dev) {
        ipPacketsSent.computeIfAbsent(dev, (n) -> new AtomicInteger()).incrementAndGet();
    }
    public void forwardPacket(NetDevice dev, IpPacket packet) {
        ipPacketsForwarded.computeIfAbsent(dev, (n) -> new ArrayList<>()).add(packet);
    }
    public int getSentCount(NetDevice dev) {
        return ipPacketsSent.getOrDefault(dev, new AtomicInteger()).get();
    }
    public int getReceivedCount() {
        return ipPacketsReceived.size();
    }
    public List<IpPacket> getForwardedPackets(NetDevice dev) {
        return ipPacketsForwarded.getOrDefault(dev, new ArrayList<>());
    }
}
