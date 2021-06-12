package io.qala.networking.ipv4;

import io.qala.networking.Bytes;
import io.qala.networking.l1.Cable;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IpPacketTypeTest {

    @Test public void p2p_looksUpNeighborMacIfPacketToBeDeliveredOnLan() {
        Host host1 = new Host();
        Host host2 = new Host();
        new Cable(host1.net1.eth, host2.net1.eth);
        host1.getRoutingTables().main().addRoute(host2.net1.network, null, host1.net1.dev);
        host1.getIpPacketType().send(host2.net1.ipAddress, new Bytes(1, 2, 3));
        assertEquals(host1.net1.ipAddress, host2.getIpStats().getReceivedPackets(host2.net1.dev).get(0).src());
    }
    @Test public void localDelivery_ifPacketDestinationIsLocal_noNeedToSendItOut() {
        Host host1 = new Host();
        Network net2 = host1.addNet();
        host1.getIpPacketType().send(net2.ipAddress, new Bytes());
        IpPacket receivedPacket = host1.getIpStats().getReceivedPackets(net2.dev).get(0);
        assertEquals(host1.nets.get(1).ipAddress, receivedPacket.src());// don't know if this is right
        assertEquals(host1.nets.get(1).ipAddress, receivedPacket.dst());
    }

}