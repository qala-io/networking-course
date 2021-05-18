package io.qala.networking.ipv4;

import io.qala.networking.l2.L2Packet;

public interface PacketType {
    static PacketType[] createAllPacketTypes() {
        return createAllPacketTypes(new RoutingTables());
    }
    static PacketType[] createAllPacketTypes(RoutingTables rtables) {
        return new PacketType[] {new ArpPacketType(rtables)};
    }
    /**
     * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/net/ipv4/arp.c#L1290">pt_prev->func()</a>
     */
    void rcv(L2Packet l2);
    boolean matches(L2Packet l2);
}
