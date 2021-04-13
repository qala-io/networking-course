package io.qala.networking.l2;

import io.qala.networking.ipv4.ArpPacket;
import io.qala.networking.ipv4.IpAddress;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BridgeTest {
    @Test public void arpRequestIsBroadcastedToEveryPortAcceptTheSender() {
        SpyNic[] nics = new SpyNic[]{new SpyNic(), new SpyNic(), new SpyNic()};

        new Bridge(nics).route(nics[0], ArpPacket.req(Mac.random(), IpAddress.random(), Mac.BROADCAST, IpAddress.random()).toL2());
        assertEquals(0, nics[0].receivedArpRequests());
        assertEquals(1, nics[1].receivedArpRequests());
        assertEquals(1, nics[2].receivedArpRequests());
    }
    @Test public void routesArpReplyToSpecificMac() {
        SpyNic[] nics = new SpyNic[]{new SpyNic(), new SpyNic(), new SpyNic()};
        Mac src = Mac.random();
        Bridge bridge = new Bridge(nics);
        ArpPacket request = ArpPacket.req(src, IpAddress.random(), Mac.BROADCAST, IpAddress.random());

        bridge.route(nics[0], request.toL2());
        bridge.route(nics[1], request.createReply(Mac.random()).toL2());
        assertEquals(1, nics[0].receivedArpReplies());
        assertEquals(0, nics[1].receivedArpReplies());
        assertEquals(0, nics[2].receivedArpReplies());
    }
}