package io.qala.networking;

import io.qala.networking.ipv4.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class NetDeviceTest {
    @Test public void addsDeviceIpAddressToRoutingTables() {
        RoutingTables rtables = new RoutingTables();
        PacketType[] packetTypes = PacketType.createAllPacketTypes(new ArpTable(), rtables);

        IpRange network = IpRange.randomAddressInRange();
        NetDevice dev = new NetDevice(new SpyNic(), packetTypes, rtables);
        dev.addIpAddress(network);

        Route local = rtables.local().lookup(network.getAddress());
        assertEquals(network.getAddress(), local.getDestination().getAddress());
        assertSame(dev, local.getDev());

        Route remote = rtables.main().lookup(network.getAddress());
        assertNotEquals(network, remote.getDestination());
        assertEquals(network.toSubnet(), remote.getDestination());
        assertSame(dev, remote.getDev());
    }
}