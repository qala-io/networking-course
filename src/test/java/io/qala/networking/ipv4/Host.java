package io.qala.networking.ipv4;

import io.qala.networking.Loggers;
import io.qala.networking.TrafficStats;
import io.qala.networking.dev.BridgeSender;
import io.qala.networking.dev.NetDevice;
import io.qala.networking.l2.Bridge;

import java.util.ArrayList;
import java.util.List;

import static io.qala.datagen.RandomShortApi.numeric;

public class Host {
    private final FibTableList rtables = new FibTableList();
    public final NetDevObjects dev1;
    public final List<NetDevObjects> devs = new ArrayList<>();
    public final PacketType[] packetTypes;

    public Host() {
        this("eth" + numeric(3));
    }
    public Host(String devname) {
        packetTypes = PacketType.createAllPacketTypes(new ArpTable(), rtables);
        dev1 = addNetDev(devname);
    }
    public Host withNets(int n) {
        for (int i = devs.size(); i < n; i++)
            addNetDev();
        return this;
    }
    public NetDevObjects addNetDev(String name) {
        NetDevObjects net = new NetDevObjects(name, rtables, packetTypes);
        devs.add(net);
        return net;
    }
    public NetDevObjects addNetDev() {
        return addNetDev("eth" + numeric(3));
    }
    public Bridge addBridge(String name) {
        BridgeSender brSender = new BridgeSender();
        NetDevice brdev = new NetDevice(name, brSender, packetTypes);
        Bridge bridge = new Bridge(brdev);
        brSender.setBridge(bridge);
        this.devs.add(new NetDevObjects(brdev));
        return bridge;
    }
    public NetDevObjects dev(String name) {
        for (NetDevObjects dev : devs)
            if (dev.dev.toString().equals(name))
                return dev;
        throw new IllegalArgumentException("Couldn't find " + name);
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
