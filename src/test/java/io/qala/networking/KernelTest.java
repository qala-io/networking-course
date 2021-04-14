package io.qala.networking;

import io.qala.networking.ipv4.IpAddress;
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
        assertEquals(1, k2.getTrafficStats().getReceivedPackets(nic2));
    }

    @Test public void hopsToNextRouterIfGatewayIsConfigured() {
        NetworkId network = NetworkId.random();

        Kernel k1 = new Kernel();
        EthNic nic1 = new EthNic(IpAddress.random(), k1);
        k1.getRoutes().addLocalRoute(network, nic1);
    }
}