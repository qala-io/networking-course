package io.qala.networking.l2;

import io.qala.networking.Bytes;
import io.qala.networking.SpyNic;
import io.qala.networking.ipv4.ArpPacket;
import io.qala.networking.ipv4.Host;
import io.qala.networking.ipv4.IpAddress;
import io.qala.networking.l1.Cable;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class BridgeTest {
    @Test public void arpRequestIsBroadcastedToEveryPortAcceptTheSender() {
        Host h = new Host("eth0");
        SpyNic spyNic = new SpyNic();
        new Cable(spyNic, h.dev1.eth);
        Bridge br = h.addBridge("br0");
        br.addInterface(h.dev1.dev);
        br.addInterface(h.addNetDev("eth1").dev);
        br.addInterface(h.addNetDev("eth2").dev);

        h.dev("eth0").eth.receive(ArpPacket.req(Mac.random(), IpAddress.random(), Mac.BROADCAST, h.dev("eth1").ipAddress).toL2().toBytes());
        List<Bytes> receivedPackets = spyNic.receivedPackets;
        assertEquals(2, receivedPackets.size());
        // check first ARP reply
        ArpPacket arpReply = new ArpPacket(new L2Packet(receivedPackets.get(0), null));
        assertEquals(h.dev("eth1").ipAddress, arpReply.srcIp());
        assertEquals(h.dev("br0").dev.getHardwareAddress(), arpReply.srcMac());
        // check 2nd ARP reply, since IP address belongs to the same host - other devices also would respond even if it's not their IP address
        arpReply = new ArpPacket(new L2Packet(receivedPackets.get(1), null));
        assertEquals(h.dev("eth1").ipAddress, arpReply.srcIp());
        assertEquals(h.dev("br0").dev.getHardwareAddress(), arpReply.srcMac());
    }
    @Test public void routesArpReplyToSpecificMac() {
//        SpyNic[] nics = new SpyNic[]{new SpyNic(), new SpyNic(), new SpyNic()};
//        Mac src = Mac.random();
//        Bridge bridge = new Bridge(nics);
//        ArpPacket request = ArpPacket.req(src, IpAddress.random(), Mac.BROADCAST, IpAddress.random());
//
//        bridge.route(nics[0], request.toL2());
//        bridge.route(nics[1], request.createReply(Mac.random()).toL2());
//        assertEquals(1, nics[0].receivedArpReplies());
//        assertEquals(0, nics[1].receivedArpReplies());
//        assertEquals(0, nics[2].receivedArpReplies());
    }
}