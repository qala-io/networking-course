package io.qala.networking.ipv4;

import io.qala.networking.Nic;

import java.util.HashMap;
import java.util.Map;

public class RoutingTable {
    private final IpAddress defaultGateway = new IpAddress(0);
    private final Map<NetworkId, Nic> table = new HashMap<>();

    public Nic getNic(IpAddress ip) {
        for (Map.Entry<NetworkId, Nic> entry : table.entrySet())
            if (entry.getKey().matches(ip))
                return entry.getValue();
        return null;
    }
    public IpAddress getDefaultGateway() {
        return defaultGateway;
    }
}
