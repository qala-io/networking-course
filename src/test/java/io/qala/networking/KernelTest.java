package io.qala.networking;

import io.qala.networking.ipv4.IpAddress;
import io.qala.networking.ipv4.IpPacket;
import io.qala.networking.ipv4.NetworkId;
import io.qala.networking.l2.Bridge;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KernelTest {

    @Test public void requestsForMacAddressBeforeSendingIpPacketOnSameNetwork() {
        NetworkId network = NetworkId.random();

        Kernel k1 = new Kernel();
        EthNic nic1 = new EthNic(IpAddress.random(), k1);
        k1.getRoutes().addLocalRoute(network, nic1);

        Kernel k2 = new Kernel();
        IpAddress dstIp = network.randomAddr();
        EthNic nic2 = new EthNic(dstIp, k2);

        new Bridge(nic1, nic2);
        k1.send(dstIp, new Bytes("hello".getBytes()));
        assertEquals(nic2.getMac(), k1.getArpTable().get(dstIp));
        assertEquals(1, k1.getTrafficStats().getSentPackets(nic1));
        assertEquals(1, k2.getTrafficStats().getReceivedPackets(nic2).size());
    }

    /**
     * Sending IpPacket(dst: 1.1.1.1)
     * k1 sees that it matches the destination 1.1.1.0 and so it uses gateway 2.2.2.2
     * k1 sends an IP packet to gateway's MAC with dst IP 1.1.1.1
     * gateway sends IP 1.1.1.1 with final node's MAC
     */
    @Test public void hopsToNextRouterIfGatewayIsConfigured() {
        NetworkId network = new NetworkId("1.1.1.0/24");
        Kernel k1 = new Kernel();
        EthNic nic1 = new EthNic(new IpAddress("127.0.0.1"), k1);
        IpAddress gateway = new IpAddress("2.2.2.2");

        Kernel routerK = new Kernel();
        EthNic routerNic = new EthNic(gateway, routerK);
        new Bridge(nic1, routerNic);

        k1.getRoutes().addGatewayRoute(network, gateway, nic1);
        k1.send(new IpAddress("1.1.1.1"), new Bytes("world".getBytes()));

        IpPacket receivedPacket = routerK.getTrafficStats().getReceivedPackets(routerNic).get(0);
        assertEquals(routerNic.getMac(), receivedPacket.dstMac());
        assertEquals(new IpAddress("1.1.1.1"), receivedPacket.dst());
    }
}