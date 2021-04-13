package io.qala.networking;

import io.qala.networking.ipv4.ArpPacket;
import io.qala.networking.ipv4.IpAddress;
import io.qala.networking.ipv4.NetworkId;
import io.qala.networking.l2.Bridge;
import io.qala.networking.l2.Mac;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RealisticNetworkTest {
    @Test public void lan() {
        NetworkId network = new NetworkId("10.0.0.0/16");
        List<IpAddress> hosts = hosts(network, 3);
        Bridge bridge = new Bridge(
                new EthNic(Mac.random(), hosts.get(0), new Kernel()),
                new EthNic(Mac.random(), hosts.get(1), new Kernel()),
                new EthNic(Mac.random(), hosts.get(2), new Kernel())
        );
        bridge.receive(new Port(0), ArpPacket.req(Mac.random(), Mac.random(), hosts.get(0), hosts.get(1)).toL2());
    }
    private static List<IpAddress> hosts(NetworkId network, int nOfHosts) {
        List<IpAddress> result = new ArrayList<>(nOfHosts);
        for (int i = 0; i < nOfHosts; i++)
            result.add(network.randomAddr());
        return result;
    }
}
