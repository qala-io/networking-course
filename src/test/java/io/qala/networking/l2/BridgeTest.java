package io.qala.networking.l2;

import io.qala.networking.IpAddress;
import io.qala.networking.PacketFactory;
import io.qala.networking.Port;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BridgeTest {
    @Test public void arpRequestIsDeliveredToEveryPort() {
        Mac src = new Mac(new byte[]{1,2,3,4,5,6});
        IpAddress targetIp = new IpAddress("192.168.0.2");

        Bridge bridge = new Bridge();

        Port port0 = new Port(0);
        SpyNic port0Endpoint = new SpyNic(),
                    port1Endpoint = new SpyNic(),
                    port2Endpoint = new SpyNic();
        bridge.attachWire(port0, port0Endpoint);
        bridge.attachWire(new Port(1), port1Endpoint);
        bridge.attachWire(new Port(2), port2Endpoint);

        bridge.receive(port0, PacketFactory.arpRequest(src, targetIp));
        assertEquals(0, port0Endpoint.size());
        assertEquals(1, port1Endpoint.size());
        assertEquals(1, port2Endpoint.size());
    }
}