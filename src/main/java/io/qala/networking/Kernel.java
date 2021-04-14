package io.qala.networking;

import io.qala.networking.ipv4.*;
import io.qala.networking.l2.ArpTable;
import io.qala.networking.l2.Mac;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Kernel {
    private static final Logger LOGGER = LoggerFactory.getLogger(Kernel.class);
    private final Routes routes = new Routes();
    private final ArpTable arpTable = new ArpTable();
    private final TrafficStats trafficStats = new TrafficStats();

    public void process(ArpPacket packet) {

    }
    public void process(Nic nic, IpPacket ipPacket) {
        trafficStats.receivedPacket(nic);
    }
    public void send(IpAddress dstIp, Bytes payload) {
        Route route = routes.getRoute(dstIp);
        Nic nic = route.getNic();
        if(route.isLocal()) {
            Mac dstMac = arpTable.getOrCompute(dstIp, () -> nic.sendArp(dstIp));
            nic.send(dstIp, dstMac, payload);
            trafficStats.sentPacket(nic);
        } else {
            //route
        }
    }
    public void route(IpPacket ipPacket) {

    }
    public void putArpEntry(IpAddress ip, Mac src) {
        arpTable.put(ip, src);
    }

    ArpTable getArpTable() {
        return arpTable;
    }
    Routes getRoutes() {
        return routes;
    }
    TrafficStats getTrafficStats() {
        return trafficStats;
    }
}
