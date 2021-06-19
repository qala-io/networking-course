package io.qala.networking;

import io.qala.networking.dev.NetDevice;
import io.qala.networking.l2.L2Packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TrafficStats {
    private final HashMap<NetDevice, List<L2Packet>> ipPacketsReceived = new HashMap<>();
    private final HashMap<NetDevice, AtomicInteger> ipPacketsSent = new HashMap<>();
    private final HashMap<NetDevice, List<L2Packet>> ipPacketsForwarded = new HashMap<>();

    public void receivedPacket(NetDevice dev, L2Packet packet) {
        ipPacketsReceived.computeIfAbsent(dev, (n) -> new ArrayList<>()).add(packet);
    }
    public List<L2Packet> getReceivedPackets(NetDevice dev) {
        return ipPacketsReceived.getOrDefault(dev, new ArrayList<>());
    }
    public void sentPacket(NetDevice dev) {
        ipPacketsSent.computeIfAbsent(dev, (n) -> new AtomicInteger()).incrementAndGet();
    }
    public void forwardPacket(NetDevice dev, L2Packet packet) {
        ipPacketsForwarded.computeIfAbsent(dev, (n) -> new ArrayList<>()).add(packet);
    }
    public int getSentCount(NetDevice dev) {
        return ipPacketsSent.getOrDefault(dev, new AtomicInteger()).get();
    }
    public int getReceivedCount() {
        return ipPacketsReceived.size();
    }
    public List<L2Packet> getForwardedPackets(NetDevice dev) {
        return ipPacketsForwarded.getOrDefault(dev, new ArrayList<>());
    }
}
