package io.qala.networking;

import io.qala.networking.ipv4.IpAddress;
import io.qala.networking.ipv4.Route;
import io.qala.networking.ipv4.FibTable;

public class Kernel {
    private final FibTable routes = new FibTable();
    public void send(IpAddress dstIp, Bytes payload) {
        Route route = routes.lookup(dstIp);
        NetDevice nic = route.getDev();
        // if it's a local route - same IP is returned, otherwise the gateway is returned
        IpAddress nextHopIp = route.getNextHopFor(dstIp);
//        IpAddress srcIp = nicIpAddress.get(nic);
//        Mac dstMac = arpTable.getOrCompute(nextHopIp, () -> nic.sendArp(srcIp, nextHopIp));
//        nic.send(srcIp, dstIp, dstMac, payload);
//        trafficStats.sentPacket(nic);
    }
}
