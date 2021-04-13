package io.qala.networking.l2;

import io.qala.networking.ipv4.ArpPacket;
import io.qala.networking.ipv4.IpAddress;
import io.qala.networking.Port;
import io.qala.networking.ipv4.NetworkId;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BridgeTest {
    @Test public void arpRequestIsDeliveredToEveryPort() {
        Mac src = new Mac(new byte[]{1,2,3,4,5,6});
        NetworkId network = new NetworkId(new IpAddress("192.0.2.128"), new IpAddress("255.255.255.192"));

        SpyNic port0Endpoint = new SpyNic(),
                port1Endpoint = new SpyNic(),
                port2Endpoint = new SpyNic();
        Bridge bridge = new Bridge(port0Endpoint, port1Endpoint, port2Endpoint);

        bridge.receive(port0Endpoint, ArpPacket.req(src, Mac.random(), network.randomAddr(), network.randomAddr()).toL2());
        assertEquals(0, port0Endpoint.size());
        assertEquals(1, port1Endpoint.size());
        assertEquals(1, port2Endpoint.size());
    }
}