package io.qala.networking;

import io.qala.networking.ipv4.*;
import io.qala.networking.l2.ArpTable;
import io.qala.networking.l2.Mac;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Kernel {
    private static final Logger LOGGER = LoggerFactory.getLogger(Kernel.class);
    private final RoutingTable routes = new RoutingTable();
    private final ArpTable arpTable = new ArpTable();

    public void process(ArpPacket packet) {

    }
    public void process(IpPacket ipPacket) {

    }
    public void send(IpAddress dstIp, Bytes payload) {
        Nic nic = routes.getNic(dstIp);
        if(nic == null) {
            routes.getDefaultGateway();
            // send to the router
        }
        Mac dstMac = arpTable.getOrCompute(dstIp, () -> nic.sendArp(dstIp));
        nic.send(dstIp, dstMac, payload);
    }
    public void route(IpPacket ipPacket) {
        IpAddress dstIp = ipPacket.dst();
        Nic nic = routes.getNic(dstIp);
        if(nic == null) {
            routes.getDefaultGateway();
            // send to the router
        }
        Mac dstMac = arpTable.get(dstIp);
        if(dstMac == null)// in real life this is async and OS has to wait until ARP table is updated
            nic.sendArp(dstIp);
        nic.send(dstIp, dstMac, ipPacket.getPayload());
    }
    public void putArpEntry(IpAddress ip, Mac src) {
        arpTable.put(ip, src);
    }

    public void addRoute(NetworkId networkId, Nic nic) {
        Loggers.TERMINAL_COMMANDS.info("ip route add to local {} dev {}", networkId, nic);
        routes.put(networkId, nic);
    }
    ArpTable getArpTable() {
        return arpTable;
    }
}
