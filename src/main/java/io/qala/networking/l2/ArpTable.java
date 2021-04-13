package io.qala.networking.l2;

import io.qala.networking.ipv4.IpAddress;

import java.util.HashMap;
import java.util.Map;

public class ArpTable {
    private final Map<IpAddress, Mac> dynamicIps = new HashMap<>();

    public void put(IpAddress ip, Mac mac) {
        dynamicIps.put(ip, mac);
    }
}
