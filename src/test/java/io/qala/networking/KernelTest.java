package io.qala.networking;

import io.qala.networking.ipv4.IpAddress;
import io.qala.networking.ipv4.NetworkId;
import io.qala.networking.l2.Bridge;
import io.qala.networking.l2.Mac;
import org.junit.Test;

import static org.junit.Assert.*;

public class KernelTest {

    @Test public void requestsForMacAddressBeforeSendingIpPacketOnSameNetwork() {
        NetworkId network = NetworkId.random();
        Kernel k = new Kernel();
        IpAddress dstIp = network.randomAddr();
        EthNic nic1 = new EthNic(Mac.random(), IpAddress.random(), k);
        EthNic nic2 = new EthNic(Mac.random(), dstIp, new Kernel());
        new Bridge(nic1, nic2);
        k.addRoute(network, nic1);
        k.send(dstIp, new Bytes("hello world".getBytes()));
        assertEquals(nic2.getMac(), k.getArpTable().get(dstIp));
    }
}