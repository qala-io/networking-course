package io.qala.networking;

import io.qala.networking.ipv4.ArpPacket;
import io.qala.networking.ipv4.IpAddress;
import io.qala.networking.l2.Mac;
import io.qala.networking.l2.SpyBridge;
import org.junit.Test;

import static org.junit.Assert.*;

public class EthNicTest {
    @Test public void respondsToArpRequest_ifDstIpMatches() {
        SpyBridge bridge = new SpyBridge();
        Mac dstMac = Mac.random();
        IpAddress dstIp = IpAddress.random();
        Nic nic = new EthNic(dstMac, dstIp, null);
        nic.setEndpoint(bridge);

        nic.receive(null, ArpPacket.req(Mac.random(), IpAddress.random(), dstMac, dstIp).toL2());
        assertEquals(1, bridge.getReceivedReplies().size());
        assertEquals(dstMac, bridge.getReceivedReplies().get(0).src());
    }
    @Test public void dropsToArpRequest_ifDstIpDoesNotMatch() {
        SpyBridge bridge = new SpyBridge();
        Nic nic = new EthNic(Mac.random(), IpAddress.random(), null);
        nic.setEndpoint(bridge);

        nic.receive(null, ArpPacket.req(Mac.random(), IpAddress.random(), Mac.random(), IpAddress.random()).toL2());
        assertEquals(0, bridge.getReceivedReplies().size());
    }

    @Test public void storesResultOfArpReplyToArpTable() {
        Kernel k = new Kernel();
        Nic nic = new EthNic(Mac.random(), IpAddress.random(), k);

        Mac dstMac = Mac.random();
        ArpPacket req = ArpPacket.req(Mac.random(), IpAddress.random(), Mac.BROADCAST, IpAddress.random());
        nic.receive(null, req.createReply(dstMac).toL2());
        assertEquals(dstMac, k.getArpTable().get(req.dstIp()));
    }
    @Test public void repliesNothingToArpReply() {
        SpyBridge bridge = new SpyBridge();
        Nic nic = new EthNic(Mac.random(), IpAddress.random(), new Kernel());
        nic.setEndpoint(bridge);

        nic.receive(null, ArpPacket.req(Mac.random(), IpAddress.random(), Mac.random(), IpAddress.random()).toL2());
        assertEquals(0, bridge.getReceivedReplies().size());
        assertEquals(0, bridge.getReceivedReqs().size());
    }
}