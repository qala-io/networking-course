package io.qala.networking.ipv4;

import java.util.ArrayList;
import java.util.List;

public class Host {
    private final RoutingTables rtables = new RoutingTables();
    final Network net1;
    final List<Network> nets = new ArrayList<>();
    final PacketType[] packetTypes;

    public Host() {
        packetTypes = PacketType.createAllPacketTypes(new ArpTable(), rtables);
        net1 = new Network(rtables, packetTypes);
        nets.add(net1);
    }
    public Host withNets(int n) {
        for (int i = nets.size(); i < n; i++)
            addNet();
        return this;
    }
    public void addNet() {
        Network net = new Network(rtables, packetTypes);
        nets.add(net);
    }

    ArpTable getArpTable() {
        return (getArpPacketType()).getArpTable();
    }
    public ArpPacketType getArpPacketType() {
        return (ArpPacketType) packetTypes[0];
    }
    public IpPacketType getIpPacketType() {
        return (IpPacketType) packetTypes[1];
    }
}
