package io.qala.networking;

import io.qala.networking.ipv4.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class NetDeviceTest {
    @Test public void addsDeviceIpAddressToLocalRoutingTable() {
        RoutingTables rtables = new RoutingTables();
        PacketType[] packetTypes = PacketType.createAllPacketTypes(rtables);

        IpRange network = IpRange.randomAddressInRange();
        NetDevice dev = new NetDevice(new SpyNic(), packetTypes, rtables);
        dev.addIpAddress(network);

        Route route = rtables.local().lookup(network.getAddress());
        assertEquals(network.getAddress(), route.getDestination().getAddress());
        assertSame(dev, route.getDev());
    }
}