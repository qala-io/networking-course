package io.qala.networking.ipv4;

import io.qala.networking.EthNic2;
import io.qala.networking.NetDevice;
import io.qala.networking.SpyNic;
import io.qala.networking.l1.Cable;
import io.qala.networking.l2.L2Packet;
import io.qala.networking.l2.Mac;
import org.junit.Test;

import static org.junit.Assert.*;

public class ArpPacketTypeTest {
    @Test public void respondsToArpReq_ifDstIpMatches() {
        NetworkId network = NetworkId.random();
        IpAddress dstIp = network.randomAddr();
        EthNic2 eth = new EthNic2();
        SpyNic senderNic = new SpyNic();
        new Cable(eth, senderNic);
        new NetDevice(eth, PacketType.createAllPacketTypes()).addIpAddress(dstIp, network);

        ArpPacket arpReq = ArpPacket.req(Mac.random(), IpAddress.random(), Mac.BROADCAST, dstIp);
        eth.receive(arpReq.toL2().toBytes());
        assertEquals(1, senderNic.receivedPackets.size());

        ArpPacket arpReply = new ArpPacket(new L2Packet(senderNic.receivedPackets.get(0), null));
        assertTrue(arpReply.isReply());
        assertEquals(arpReq.srcMac(), arpReply.dstMac());
        assertEquals(eth.getMac(), arpReply.srcMac());
        assertEquals(dstIp, arpReply.srcIp());
        assertEquals(arpReq.srcIp(), arpReply.dstIp());
    }
    @Test public void dropsArpReq_ifDstIpDoesNotMatch() {
        EthNic2 eth = new EthNic2();
        SpyNic senderNic = new SpyNic();
        new Cable(eth, senderNic);
        NetworkId network = NetworkId.random();
        new NetDevice(eth, PacketType.createAllPacketTypes()).addIpAddress(network.randomAddr(), network);

        ArpPacket arpReq = ArpPacket.req(Mac.random(), IpAddress.random(), Mac.BROADCAST, IpAddress.random());
        eth.receive(arpReq.toL2().toBytes());
        assertEquals(0, senderNic.receivedPackets.size());
    }
    @Test public void storesReceivedArpReq_intoArpTable() {
        PacketType[] packetTypes = PacketType.createAllPacketTypes();
        NetworkId network = NetworkId.random();
        IpAddress dstIp = network.randomAddr();
        EthNic2 eth = new EthNic2();
        new Cable(eth, new SpyNic());
        new NetDevice(eth, packetTypes).addIpAddress(dstIp, network);

        ArpPacket arpReq = ArpPacket.req(Mac.random(), IpAddress.random(), Mac.BROADCAST, dstIp);
        eth.receive(arpReq.toL2().toBytes());
        ArpTable arpTable = ((ArpPacketType) packetTypes[0]).getArpTable();
        assertEquals(arpReq.srcMac(), arpTable.get(arpReq.srcIp()));
    }
    @Test public void storesResultOfArpReplyToArpTable() {
        HostNetwork host1 = new HostNetwork();
        HostNetwork host2 = new HostNetwork();
        new Cable(host1.eth, host2.eth);

        ArpPacket arpReq = ArpPacket.req(host1.net.getMac(), host1.ipAddress, Mac.BROADCAST, host2.ipAddress);
        host1.eth.send(arpReq.toL2());

        assertEquals(host2.eth.getMac(), host1.getArpTable().get(host2.ipAddress));
        assertEquals(host1.eth.getMac(), host2.getArpTable().get(host1.ipAddress));
    }

    static class HostNetwork {
        final NetworkId network;
        final IpAddress ipAddress;
        final EthNic2 eth;
        final PacketType[] packetTypes = PacketType.createAllPacketTypes();
        final NetDevice net;

        public HostNetwork() {
            network = NetworkId.random();
            ipAddress = network.randomAddr();
            eth = new EthNic2();
            net = new NetDevice(eth, packetTypes);
            net.addIpAddress(ipAddress, network);
        }

        ArpTable getArpTable() {
            return ((ArpPacketType) packetTypes[0]).getArpTable();
        }
    }
}