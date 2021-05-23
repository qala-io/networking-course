package io.qala.networking.ipv4;

public class Host {
    final Network net1, net2;
    final PacketType[] packetTypes;

    public Host() {
        RoutingTables rtables = new RoutingTables();
        packetTypes = PacketType.createAllPacketTypes(rtables);
        net1 = new Network(rtables, packetTypes);
        net2 = new Network(rtables, packetTypes);
    }

    ArpTable getArpTable() {
        return ((ArpPacketType) packetTypes[0]).getArpTable();
    }
}
