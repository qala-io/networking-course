package io.qala.networking;

import io.qala.networking.ipv4.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class NetDeviceTest {
    @Test public void addsDeviceIpAddressToRoutingTables_whenSettingDeviceIpAddress() {
        IpRange network = IpRange.randomAddressInRange();
        Host host = new Host();
        NetDevice dev = host.dev1.dev;
        dev.addIpAddress(network);

        Route local = host.getRoutingTables().local().lookup(network.getAddress());
        assertEquals(network.getAddress(), local.getDestination().getAddress());
        assertSame(dev, local.getDev());

        Route remote = host.getRoutingTables().main().lookup(network.getAddress());
        assertNotEquals(network, remote.getDestination());
        assertEquals(network.toSubnet(), remote.getDestination());
        assertSame(dev, remote.getDev());
    }
}