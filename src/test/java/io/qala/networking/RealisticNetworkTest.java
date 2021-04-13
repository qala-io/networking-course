package io.qala.networking;

import io.qala.networking.ipv4.ArpPacket;
import io.qala.networking.ipv4.IpAddress;
import io.qala.networking.ipv4.NetworkId;
import io.qala.networking.l2.Bridge;
import io.qala.networking.l2.Mac;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RealisticNetworkTest {
    @Test public void physicalLan() {
        NetworkId network = new NetworkId("10.0.0.0/16");
        List<IpAddress> hosts = hosts(network, 3);
        Kernel kernel1 = new Kernel();
        EthNic nic1 = new EthNic(Mac.random(), hosts.get(0), kernel1);
        EthNic nic2 = new EthNic(Mac.random(), hosts.get(1), new Kernel());
        Bridge bridge = new Bridge(nic1, nic2, new EthNic(Mac.random(), hosts.get(2), new Kernel()));
        bridge.route(nic1, ArpPacket.req(Mac.random(), Mac.BROADCAST, hosts.get(0), hosts.get(1)).toL2());
        assertEquals(nic2.getMac(), kernel1.getArpTable().get(hosts.get(1)));
    }
    private static List<IpAddress> hosts(NetworkId network, int nOfHosts) {
        List<IpAddress> result = new ArrayList<>(nOfHosts);
        for (int i = 0; i < nOfHosts; i++)
            result.add(network.randomAddr());
        return result;
    }
}
