package io.qala.networking.ipv4;

import io.qala.networking.dev.NetDevice;
import io.qala.networking.l2.Mac;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ArpTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArpTable.class.getSimpleName());
    private final Map<IpAddress, Mac> dynamicIps = new HashMap<>();

    public void put(IpAddress ip, Mac mac) {
        LOGGER.trace("Updating ARP table with [{} -> {}]", ip, mac);
        dynamicIps.put(ip, mac);
    }
    public Mac getNeighbor(IpAddress dst, NetDevice dev) {
        Mac mac = getFromCache(dst, dev);
        if(mac == null) {
            // does the ArpTable prepares the data for the request itself or the ArpRequestType?
            // in real life this is async and OS has to wait until ARP table is updated
            ArpPacket arpReq = ArpPacket.req(dev.getHardwareAddress(), dev.getIpAddress(), Mac.BROADCAST, dst);
            dev.send(arpReq.toL2());
            mac = getFromCache(dst, dev);
        }
        if(mac == null)
            throw new RuntimeException("Couldn't get MAC address for IP " + dst);
        return mac;
    }
    public Mac getFromCache(IpAddress dst, NetDevice dev) {
        return dynamicIps.get(dst);
    }
}
