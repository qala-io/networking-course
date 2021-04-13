package io.qala.networking;

import io.qala.networking.ipv4.ArpPacket;
import io.qala.networking.ipv4.IpAddress;
import io.qala.networking.ipv4.IpPacket;
import io.qala.networking.ipv4.Router;
import io.qala.networking.l2.ArpTable;
import io.qala.networking.l2.Mac;

import java.util.ArrayList;
import java.util.List;

public class Kernel {
    private final List<Nic> nics = new ArrayList<>();
    private final Router routes = new Router();
    private final ArpTable arpTable = new ArpTable();

    public void process(ArpPacket packet) {

    }
    public void process(IpPacket ipPacket) {

    }
    public void route(IpPacket ipPacket) {

    }
    public void putArpEntry(IpAddress ip, Mac src) {
        arpTable.put(ip, src);
    }
}
