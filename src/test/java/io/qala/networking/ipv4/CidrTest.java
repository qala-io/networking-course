package io.qala.networking.ipv4;

import org.junit.Test;

import static org.junit.Assert.*;

public class CidrTest {
    @Test public void parsesStringBasedNetworkMask() {
        Cidr typeC = new Cidr("168.0.12.0/24");
        assertTrue(typeC.matches(new IpAddress("168.0.12.1")));
        assertTrue(typeC.matches(new IpAddress("168.0.12.255")));
        assertFalse(typeC.matches(new IpAddress("168.1.12.1")));
        assertFalse(typeC.matches(new IpAddress("168.0.13.1")));
        assertFalse(typeC.matches(new IpAddress("167.0.12.0")));

        Cidr networkId = new Cidr("192.0.2.128/25");
        assertTrue(networkId.matches(new IpAddress("192.0.2.129")));
        assertTrue(networkId.matches(new IpAddress("192.0.2.255")));
        assertTrue(networkId.matches(new IpAddress("192.0.2.128")));
        assertFalse(networkId.matches(new IpAddress("192.0.2.1")));
        assertFalse(networkId.matches(new IpAddress("192.1.12.1")));
        assertFalse(networkId.matches(new IpAddress("192.0.13.1")));
        assertFalse(networkId.matches(new IpAddress("167.0.12.0")));
    }
    @Test public void randomAlwaysGeneratesIpWithinTheNetwork() {
        Cidr typeC = new Cidr("168.0.12.0/24");
        IpAddress address = typeC.randomAddr();
        assertTrue("actual: " + address, typeC.matches(address));
    }
    @Test public void canParseNetworkAndThenGenerateSameAsString() {
        String net = "168.0.12.0/23";
        assertEquals(net, new Cidr(net).toString());
    }
}