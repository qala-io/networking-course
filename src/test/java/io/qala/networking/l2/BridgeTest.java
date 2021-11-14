package io.qala.networking.l2;

import io.qala.networking.Bytes;
import io.qala.networking.SpyNic;
import io.qala.networking.ipv4.ArpPacket;
import io.qala.networking.ipv4.Host;
import io.qala.networking.ipv4.IpAddress;
import io.qala.networking.ipv4.IpPacket;
import io.qala.networking.l1.Cable;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        // todo:
        // it seems like there should be a bridge MAC: https://unix.stackexchange.com/questions/654241/how-does-arp-process-replies-to-the-right-device#comment1231562_654251
        // but it's not clear how the next query that comes to this host will be "forwarded" to the right port
        // if it's going to contain Bridge MAC instead of eth1 MAC
        assertEquals(h.dev("eth1").dev.getHardwareAddress(), arpReply.srcMac());
        // check 2nd ARP reply, since IP address belongs to the same host - other devices also would respond even if it's not their IP address
        arpReply = new ArpPacket(new L2Packet(receivedPackets.get(1), null));
        assertEquals(h.dev("eth1").ipAddress, arpReply.srcIp());
        assertEquals(h.dev("eth2").dev.getHardwareAddress(), arpReply.srcMac());
    }
    @Test public void routesArpReplyToSpecificMac() {
        Host h = new Host("eth0");
        SpyNic spyNic = new SpyNic();
        new Cable(spyNic, h.dev1.eth);
        Bridge br = h.addBridge("br0");
        br.addInterface(h.dev1.dev);
        br.addInterface(h.addNetDev("eth1").dev);

        ArpPacket request = ArpPacket.req(Mac.random(), IpAddress.random(), Mac.BROADCAST, h.dev("eth1").ipAddress);
        h.dev("eth0").eth.receive(request.toL2().toBytes());

        assertEquals(1, spyNic.receivedPackets.size());

        ArpPacket arpReply = new ArpPacket(new L2Packet(spyNic.receivedPackets.get(0), null));
        assertTrue(arpReply.isReply());
        assertEquals(request.srcMac(), arpReply.dstMac());
        assertEquals(h.dev("eth1").ipAddress, arpReply.srcIp());
    }

    @Test public void deliversIpPacketsThroughBridgeToTargetEth() {
        Host h = new Host("eth0");
        new Cable(new SpyNic(), h.dev1.eth);
        Bridge br = h.addBridge("br0");
        br.addInterface(h.dev1.dev);
        br.addInterface(h.addNetDev("eth1").dev);

        IpPacket ipPacket = new IpPacket(
                Mac.random(), IpAddress.random(),
                h.dev("eth1").eth.getMac(), h.dev("eth1").ipAddress,
                new Bytes());
        h.dev("eth0").eth.receive(ipPacket.toL2().toBytes());

        List<L2Packet> eth1Received = h.getIpStats().getReceivedPackets(h.dev("eth1").dev);
        assertEquals(1, eth1Received.size());
    }
}