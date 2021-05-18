package io.qala.networking.ipv4;

import io.qala.networking.Loggers;
import io.qala.networking.NetDevice;

import java.util.HashMap;
import java.util.Map;

public class RoutingTable {
    private Route defaultGateway;
    private final Map<NetworkId, Route> table = new HashMap<>();
    public RoutingTable() { }
    public RoutingTable(IpAddress defaultGateway, NetDevice device) {
         addDefaultRoute(defaultGateway, device);
    }
    public void addDefaultRoute(IpAddress gateway, NetDevice dev) {
        Loggers.TERMINAL_COMMANDS.info("ip route add default via {} dev {}", gateway, dev);
        NetworkId defaultDestination = new NetworkId(IpAddress.MIN, IpAddress.MAX);
        this.defaultGateway = new Route(defaultDestination, gateway, dev, RouteType.REMOTE/*??*/);
    }
    public void addLocalRoute(NetworkId destination, NetDevice dev) {
        Loggers.TERMINAL_COMMANDS.info("ip route add to local {} dev {}", destination, dev);
        table.put(destination, new Route(destination, IpAddress.MIN, dev, RouteType.LOCAL));
    }
    public void addGatewayRoute(NetworkId destination, IpAddress gateway, NetDevice dev) {
        Loggers.TERMINAL_COMMANDS.info("ip route add {} via {} dev {}", destination, gateway, dev);
        table.put(destination, new Route(destination, gateway, dev, RouteType.REMOTE/*??*/));
    }
    public Route getRoute(IpAddress ip ) {
        for (Map.Entry<NetworkId, Route> entry : table.entrySet())
            if (entry.getKey().matches(ip))
                return entry.getValue();
        return defaultGateway;
    }
}
