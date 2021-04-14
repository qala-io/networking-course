package io.qala.networking;

import io.qala.networking.ipv4.IpAddress;
import io.qala.networking.ipv4.NetworkId;
import io.qala.networking.l2.Bridge;
import io.qala.networking.l2.Mac;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KernelTest {

    @Test public void requestsForMacAddressBeforeSendingIpPacketOnSameNetwork() {
        NetworkId network = NetworkId.random();

        Kernel k1 = new Kernel();
        EthNic nic1 = new EthNic(Mac.random(), IpAddress.random(), k1);

        Kernel k2 = new Kernel();
        IpAddress dstIp = network.randomAddr();
        EthNic nic2 = new EthNic(Mac.random(), dstIp, k2);

        new Bridge(nic1, nic2);
        k1.addRoute(network, nic1);
        k1.send(dstIp, new Bytes("hello world".getBytes()));
        assertEquals(nic2.getMac(), k1.getArpTable().get(dstIp));
        assertEquals(1, k1.getTrafficStats().getSentPackets(nic1));
        assertEquals(1, k2.getTrafficStats().getReceivedPackets(nic2));
    }

}