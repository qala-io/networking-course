package io.qala.networking.ipv4;

import io.qala.networking.Bytes;
import io.qala.networking.l1.Cable;
import io.qala.networking.l2.Mac;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IpPacketTypeTest {

    @Test public void p2p_looksUpNeighborMacIfPacketToBeDeliveredOnLan() {
        Host host1 = new Host();
        Host host2 = new Host();
        new Cable(host1.dev1.eth, host2.dev1.eth);
        host1.getRoutingTables().main().addRoute(host2.dev1.network, null, host1.dev1.dev);
        host1.getIpPacketType().send(host2.dev1.ipAddress, new Bytes(1));
        assertEquals(host1.dev1.ipAddress, new IpPacket(host2.getIpStats().getReceivedPackets(host2.dev1.dev).get(0)).src());
    }
    @Test public void localDelivery_ifPacketDestinationIsLocal_noNeedToSendItOut() {
        Host host1 = new Host();
        NetDevObjects net2 = host1.addNetDev();
        host1.getIpPacketType().send(net2.ipAddress, new Bytes());
        IpPacket receivedPacket = new IpPacket(host1.getIpStats().getReceivedPackets(net2.dev).get(0));
        assertEquals(host1.devs.get(1).ipAddress, receivedPacket.src());// don't know if this is right
        assertEquals(host1.devs.get(1).ipAddress, receivedPacket.dst());
    }
    @Test public void sendsPacketToGatewayIfRouteHasIt() {
        Host host1 = new Host();
        Host host2 = new Host();
        NetDevObjects host2net2 = host2.addNetDev();
        new Cable(host1.dev1.eth, host2.dev1.eth);
        host1.getRoutingTables().main().addRoute(host2.dev1.network, host2net2.ipAddress, host1.dev1.dev);

        host1.getIpPacketType().send(host2.dev1.ipAddress, new Bytes());
        IpPacket deliveredPacket = new IpPacket(host2.getIpStats().getReceivedPackets(host2.dev1.dev).get(0));
        assertEquals(host1.dev1.ipAddress, deliveredPacket.src());
        assertEquals(host2.dev1.ipAddress, deliveredPacket.dst());
        // even though the IP address is for host2.net2, it's host2.net1 that will receive the ARP and thus
        // we'll be sending the request to that MAC
        assertEquals(host2.dev1.eth.getMac(), deliveredPacket.dstMac());
        assertEquals(deliveredPacket.dstMac(), host1.getArpTable().getFromCache(host2net2.ipAddress, host1.dev1.dev));
    }
    @Test public void packetIsDroppedIfForwardingIsRequiredButItIsTurnedOff() {
        Host host = new Host();
        NetDevObjects externalNetwork = new Host().dev1;
        host.getRoutingTables().main().addRoute(externalNetwork.network, null, host.dev1.dev);

        host.dev1.eth.receive(new IpPacket(
                Mac.random(), IpAddress.random(),
                host.dev1.dev.getMac(), externalNetwork.ipAddress,
                new Bytes()).toL2().toBytes());
        assertEquals(0, host.getIpStats().getReceivedCount());
    }
    @Test public void forwardsPackets_ifTheirDestinationIsRemote() {
        Host host1 = new Host();
        NetDevObjects host1net2 = host1.addNetDev();
        host1.enableIpForward();
        Host host2 = new Host();
        host1.getRoutingTables().main().addRoute(host2.dev1.network, null, host1net2.dev);
        new Cable(host1net2.eth, host2.dev1.eth);

        host1.dev1.eth.receive(new IpPacket(
                Mac.random(), IpAddress.random(),
                host1.dev1.dev.getMac(), host2.dev1.ipAddress,
                new Bytes()).toL2().toBytes());
        assertEquals(1, host1.getIpStats().getForwardedPackets(host1net2.dev).size());
        assertEquals(1, host2.getIpStats().getReceivedPackets(host2.dev1.dev).size());
    }

}