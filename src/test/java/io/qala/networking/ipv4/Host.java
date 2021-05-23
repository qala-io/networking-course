package io.qala.networking.ipv4;

import java.util.ArrayList;
import java.util.List;

public class Host {
    private final RoutingTables rtables = new RoutingTables();
    final Network net1;
    final List<Network> nets = new ArrayList<>();
    final PacketType[] packetTypes;

    public Host() {
        packetTypes = PacketType.createAllPacketTypes(rtables);
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
        return ((ArpPacketType) packetTypes[0]).getArpTable();
    }
}
