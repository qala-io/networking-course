package io.qala.networking.ipv4;

import org.junit.Test;

import static org.junit.Assert.*;

public class NetworkMaskTest {
    @Test public void parsesStringBasedNetworkMask() {
        NetworkMask typeC = new NetworkMask("168.0.12.0/24");
        assertTrue(typeC.matches(new IpAddress("168.0.12.1")));
        assertTrue(typeC.matches(new IpAddress("168.0.12.255")));
        assertFalse(typeC.matches(new IpAddress("168.1.12.1")));
        assertFalse(typeC.matches(new IpAddress("168.0.13.1")));
        assertFalse(typeC.matches(new IpAddress("167.0.12.0")));

        NetworkMask networkMask = new NetworkMask("192.0.2.128/25");
        assertTrue(networkMask.matches(new IpAddress("192.0.2.129")));
        assertTrue(networkMask.matches(new IpAddress("192.0.2.255")));
        assertTrue(networkMask.matches(new IpAddress("192.0.2.128")));
        assertFalse(networkMask.matches(new IpAddress("192.0.2.1")));
        assertFalse(networkMask.matches(new IpAddress("192.1.12.1")));
        assertFalse(networkMask.matches(new IpAddress("192.0.13.1")));
        assertFalse(networkMask.matches(new IpAddress("167.0.12.0")));
    }
    @Test public void randomAlwaysGeneratesIpWithinTheNetwork() {
        NetworkMask typeC = new NetworkMask("168.0.12.0/24");
        IpAddress address = typeC.randomAddress();
        assertTrue("actual: " + address, typeC.matches(address));
    }
}