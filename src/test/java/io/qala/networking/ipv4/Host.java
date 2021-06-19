package io.qala.networking.ipv4;

import io.qala.networking.Loggers;
import io.qala.networking.dev.NetDeviceLogic;
import io.qala.networking.TrafficStats;
import io.qala.networking.l2.Bridge;

import java.util.ArrayList;
import java.util.List;

public class Host {
    private final FibTableList rtables = new FibTableList();
    public final NetDevObjects dev1;
    public final List<NetDevObjects> devs = new ArrayList<>();
    public final PacketType[] packetTypes;
    private final NetDeviceLogic netDeviceLogic;

    public Host() {
        packetTypes = PacketType.createAllPacketTypes(new ArpTable(), rtables);
        netDeviceLogic = new NetDeviceLogic(packetTypes);
        dev1 = new NetDevObjects(rtables, packetTypes);
        devs.add(dev1);
    }
    public Host withNets(int n) {
        for (int i = devs.size(); i < n; i++)
            addNetDev();
        return this;
    }
    public NetDevObjects addNetDev() {
        NetDevObjects net = new NetDevObjects(rtables, packetTypes);
        devs.add(net);
        return net;
    }
    public Bridge addBridge() {
        return new Bridge(netDeviceLogic);
    }

    public ArpTable getArpTable() {
        return (getArpPacketType()).getArpTable();
    }
    public FibTableList getRoutingTables() {
        return rtables;
    }
    public ArpPacketType getArpPacketType() {
        return (ArpPacketType) packetTypes[0];
    }
    public IpPacketType getIpPacketType() {
        return (IpPacketType) packetTypes[1];
    }
    public TrafficStats getIpStats() {
        return getIpPacketType().getTrafficStats();
    }
    public void enableIpForward() {
        Loggers.TERMINAL_COMMANDS.info("echo 1 > /proc/sys/net/ipv4/ip_forward");
        getArpPacketType().enableIpForward();
        getIpPacketType().enableIpForward();
    }
}
