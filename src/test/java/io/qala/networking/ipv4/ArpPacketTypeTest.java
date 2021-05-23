package io.qala.networking.ipv4;

import io.qala.networking.EthNic;
import io.qala.networking.NetDevice;
import io.qala.networking.SpyNic;
import io.qala.networking.l1.Cable;
import io.qala.networking.l2.L2Packet;
import io.qala.networking.l2.Mac;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArpPacketTypeTest {
    @Test public void respondsToArpReq_ifDstIpMatches() {
        Host host = new Host();
        SpyNic senderNic = new SpyNic();
        new Cable(host.net1.eth, senderNic);

        ArpPacket arpReq = ArpPacket.req(Mac.random(), IpAddress.random(), Mac.BROADCAST, host.net1.ipAddress);
        host.net1.eth.receive(arpReq.toL2().toBytes());
        assertEquals(1, senderNic.receivedPackets.size());

        ArpPacket arpReply = new ArpPacket(new L2Packet(senderNic.receivedPackets.get(0), null));
        assertTrue(arpReply.isReply());
        assertEquals(arpReq.srcMac(), arpReply.dstMac());
        assertEquals(host.net1.eth.getMac(), arpReply.srcMac());
        assertEquals(host.net1.ipAddress, arpReply.srcIp());
        assertEquals(arpReq.srcIp(), arpReply.dstIp());
    }
    /**
     * Linux owns the IP addresses, not each {@link NetDevice} separately (though they do have it too).
     */
    @Test public void respondsToArpReq_ifDstIpMatchesAnotherNetworkInterfaceOnSameHost() {
        Host host = new Host();
        SpyNic senderNic = new SpyNic();
        new Cable(host.net1.eth, senderNic);

        ArpPacket arpReq = ArpPacket.req(Mac.random(), IpAddress.random(), Mac.BROADCAST, host.net2.ipAddress);
        host.net1.eth.receive(arpReq.toL2().toBytes());
        assertEquals(1, senderNic.receivedPackets.size());

        ArpPacket arpReply = new ArpPacket(new L2Packet(senderNic.receivedPackets.get(0), null));
        assertTrue(arpReply.isReply());
        assertEquals(arpReq.srcMac(), arpReply.dstMac());
        assertEquals(host.net1.eth.getMac(), arpReply.srcMac());
        assertEquals(host.net2.ipAddress, arpReply.srcIp());
        assertEquals(arpReq.srcIp(), arpReply.dstIp());
    }
    @Test public void dropsArpReq_ifDstIpDoesNotMatch() {
        Host host = new Host();
        SpyNic senderNic = new SpyNic();
        new Cable(host.net1.eth, senderNic);

        ArpPacket arpReq = ArpPacket.req(Mac.random(), IpAddress.random(), Mac.BROADCAST, IpAddress.random());
        host.net1.eth.receive(arpReq.toL2().toBytes());
        assertEquals(0, senderNic.receivedPackets.size());
    }
    @Test public void storesReceivedArpReq_intoArpTable() {
        Host host = new Host();
        new Cable(host.net1.eth, new SpyNic());

        ArpPacket arpReq = ArpPacket.req(Mac.random(), IpAddress.random(), Mac.BROADCAST, host.net1.ipAddress);
        host.net1.eth.receive(arpReq.toL2().toBytes());
        ArpTable arpTable = host.getArpTable();
        assertEquals(arpReq.srcMac(), arpTable.get(arpReq.srcIp()));
    }
    @Test public void storesResultOfArpReplyToArpTable() {
        Host host1 = new Host();
        Host host2 = new Host();
        new Cable(host1.net1.eth, host2.net1.eth);

        ArpPacket arpReq = ArpPacket.req(host1.net1.eth.getMac(), host1.net1.ipAddress, Mac.BROADCAST, host2.net1.ipAddress);
        host1.net1.eth.send(arpReq.toL2());

        assertEquals(host2.net1.eth.getMac(), host1.getArpTable().get(host2.net1.ipAddress));
        assertEquals(host1.net1.eth.getMac(), host2.getArpTable().get(host1.net1.ipAddress));
    }

}