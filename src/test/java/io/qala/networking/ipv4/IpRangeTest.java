package io.qala.networking.ipv4;

import org.junit.Test;

import static org.junit.Assert.*;

public class IpRangeTest {
    @Test public void parsesStringBasedNetworkMask() {
        IpRange range = new IpRange("168.0.12.0/24");
        assertEquals(24, range.getNetworkBitCount());
        assertEquals(new IpAddress("168.0.12.0"), range.getAddress());

        range = new IpRange("192.0.2.128/25");
        assertEquals(25, range.getNetworkBitCount());
        assertEquals(new IpAddress("192.0.2.128"), range.getAddress());

        range = new IpRange("10.20.30.40/32");
        assertEquals(32, range.getNetworkBitCount());
        assertEquals(new IpAddress("10.20.30.40"), range.getAddress());

        range = new IpRange("0.0.0.0/0");
        assertEquals(0, range.getNetworkBitCount());
        assertEquals(new IpAddress("0.0.0.0"), range.getAddress());

        range = new IpRange("10.20.5.3/24");
        assertEquals(24, range.getNetworkBitCount());
        assertEquals(new IpAddress("10.20.5.3"), range.getAddress());
    }
    @Test public void canCreateRangeFromNumberOfNetworkBits() {
        IpRange range = new IpRange(new IpAddress("10.0.1.6"), 23);
        assertEquals("10.0.1.6/23", range.toString());
    }
    @Test public void randomAlwaysGeneratesIpWithinTheNetwork() {
        IpRange typeC = new IpRange("168.0.12.0/24");
        IpAddress address = typeC.randomAddr();
        assertTrue("actual: " + address, typeC.matches(address));
    }
    @Test public void canParseNetworkAndThenGenerateSameAsString() {
        String net = "168.0.12.0/23";
        assertEquals(net, new IpRange(net).toString());
    }
    @Test public void matchesIpAddressesInSameNetwork() {
        IpRange range = new IpRange("168.0.12.0/24");
        assertTrue(range.matches(new IpAddress("168.0.12.1")));
        assertTrue(range.matches(new IpAddress("168.0.12.255")));
        assertFalse(range.matches(new IpAddress("168.1.12.1")));
        assertFalse(range.matches(new IpAddress("168.0.13.1")));
        assertFalse(range.matches(new IpAddress("167.0.12.0")));

        range = new IpRange("192.0.2.128/25");
        assertTrue(range.matches(new IpAddress("192.0.2.129")));
        assertTrue(range.matches(new IpAddress("192.0.2.255")));
        assertTrue(range.matches(new IpAddress("192.0.2.128")));
        assertFalse(range.matches(new IpAddress("192.0.2.1")));
        assertFalse(range.matches(new IpAddress("192.1.12.1")));
        assertFalse(range.matches(new IpAddress("192.0.13.1")));
        assertFalse(range.matches(new IpAddress("167.0.12.0")));

        range = new IpRange("0.0.0.0/0");
        assertTrue(range.matches(IpAddress.random()));

        range = new IpRange("10.20.30.40/32");
        assertFalse(range.matches(IpAddress.random()));
        assertTrue(range.matches(new IpAddress("10.20.30.40")));
    }
    @Test public void canReturnSubnetWithoutExtraBits() {
        assertEquals(new IpRange("135.75.0.0/16"), new IpRange("135.75.2.1/16").toSubnet());
        assertEquals(new IpRange("135.75.2.1/32"), new IpRange("135.75.2.1/32").toSubnet());
        assertEquals(new IpRange("0.0.0.0/0"), new IpRange("0.0.0.0/0").toSubnet());
    }
}