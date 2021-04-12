package io.qala.networking.ipv4;

import io.qala.networking.Bytes;
import org.junit.Test;

import static org.junit.Assert.*;

public class IpAddressTest {
    @Test public void acceptsStringsAndBytes() {
        IpAddress ipAddress = new IpAddress(new Bytes(255, 0, 1, 68));
        assertEquals("255.0.1.68", ipAddress.toString());
        assertEquals(ipAddress, new IpAddress(ipAddress.toString()));
    }
    @Test public void strings() {
        assertEquals(new Bytes(168, 0, 12, 255), new IpAddress("168.0.12.255").toBytes());
    }
    @Test public void acceptsInt() {
        IpAddress ipAddress = new IpAddress(new Bytes(255, 0, 1, 68).toInt());
        assertEquals("255.0.1.68", ipAddress.toString());
        assertEquals(ipAddress, new IpAddress(ipAddress.toString()));
    }
}