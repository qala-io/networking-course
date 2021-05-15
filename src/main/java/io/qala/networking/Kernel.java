package io.qala.networking;

import io.qala.networking.ipv4.*;
import io.qala.networking.ipv4.ArpTable;
import io.qala.networking.l2.Mac;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class Kernel {
    private static final Logger LOGGER = LoggerFactory.getLogger(Kernel.class);
    private final ArpTable arpTable = new ArpTable();
    private final Routes routes = new Routes();
    private final TrafficStats trafficStats = new TrafficStats();
    private final HashMap<Nic, IpAddress> nicIpAddress = new HashMap<>();

    public void process(ArpPacket packet) {

    }
    public void process(Nic nic, IpPacket ipPacket) {
        trafficStats.receivedPacket(nic, ipPacket);
    }
    public void send(IpAddress dstIp, Bytes payload) {
        Route route = routes.getRoute(dstIp);
        Nic nic = route.getNic();
        // if it's a local route - same IP is returned, otherwise the gateway is returned
        IpAddress nextHopIp = route.getNextHopFor(dstIp);
        IpAddress srcIp = nicIpAddress.get(nic);
        Mac dstMac = arpTable.getOrCompute(nextHopIp, () -> nic.sendArp(srcIp, nextHopIp));
        nic.send(srcIp, dstIp, dstMac, payload);
        trafficStats.sentPacket(nic);
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
