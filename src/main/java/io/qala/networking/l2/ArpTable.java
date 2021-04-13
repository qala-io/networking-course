package io.qala.networking.l2;

import io.qala.networking.ipv4.IpAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ArpTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArpTable.class);
    private final Map<IpAddress, Mac> dynamicIps = new HashMap<>();

    public void put(IpAddress ip, Mac mac) {
        LOGGER.trace("Updating ARP table with [{} -> {}]", ip, mac);
        dynamicIps.put(ip, mac);
    }
    public Mac getOrCompute(IpAddress ip, Runnable sendingArp) {
        Mac mac = get(ip);
        if(mac == null) {
            // in real life this is async and OS has to wait until ARP table is updated
            sendingArp.run();
            mac = get(ip);
        }
        if(mac == null)
            throw new RuntimeException("Couldn't get MAC address for IP " + ip);
        return mac;
    }
    public Mac get(IpAddress ip) {
        return dynamicIps.get(ip);
    }
}
