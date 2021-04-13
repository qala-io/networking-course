package io.qala.networking;

import io.qala.networking.ipv4.ArpPacket;
import io.qala.networking.ipv4.IpAddress;
import io.qala.networking.ipv4.IpPacket;
import io.qala.networking.ipv4.RoutingTable;
import io.qala.networking.l2.ArpTable;
import io.qala.networking.l2.Mac;

import java.util.ArrayList;
import java.util.List;

public class Kernel {
    private final List<Nic> nics = new ArrayList<>();
    private final RoutingTable routes = new RoutingTable();
    private final ArpTable arpTable = new ArpTable();

    public void process(ArpPacket packet) {

    }
    public void process(IpPacket ipPacket) {

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
    ArpTable getArpTable() {
        return arpTable;
    }
}
