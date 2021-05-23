package io.qala.networking.ipv4;

import io.qala.networking.Loggers;
import io.qala.networking.NetDevice;

import java.util.HashMap;
import java.util.Map;

public class RoutingTable {
    private static final IpRange DEFAULT_ROUTE = new IpRange("0.0.0.0/0");
    private final Map<IpRange, Route> rangeRoutes = new HashMap<>();
    private final Map<IpAddress, Route> singleHostRoutes = new HashMap<>();
    public RoutingTable() { }

    public void add(Route route) {
        if(route.getDestination().getNetworkBitCount() == 32)
            singleHostRoutes.put(route.getDestination().getAddress(), route);
        else
            rangeRoutes.put(route.getDestination(), route);
    }

    public void addDefaultRoute(NetDevice dev) {
        Loggers.TERMINAL_COMMANDS.info("ip route add default dev {}", dev);
        rangeRoutes.put(DEFAULT_ROUTE, new Route(DEFAULT_ROUTE, null, dev, RouteType.LOCAL));
    }
    public void addDefaultRoute(IpAddress gateway, NetDevice dev) {
        Loggers.TERMINAL_COMMANDS.info("ip route add default via {} dev {}", gateway, dev);
        rangeRoutes.put(DEFAULT_ROUTE, new Route(DEFAULT_ROUTE, gateway, dev, RouteType.REMOTE/*??*/));
    }
    public void addLocalRoute(IpRange destination, NetDevice dev) {
        Loggers.TERMINAL_COMMANDS.info("ip route add to local {} dev {}", destination, dev);
        rangeRoutes.put(destination, new Route(destination, IpAddress.MIN, dev, RouteType.LOCAL));
    }
    public void addGatewayRoute(IpRange destination, IpAddress gateway, NetDevice dev) {
        Loggers.TERMINAL_COMMANDS.info("ip route add {} via {} dev {}", destination, gateway, dev);
        rangeRoutes.put(destination, new Route(destination, gateway, dev, RouteType.REMOTE/*??*/));
    }
    public Route getRoute(IpAddress ip) {
        Route route = singleHostRoutes.get(ip);
        if(route != null)
            return route;
        for (Map.Entry<IpRange, Route> entry : rangeRoutes.entrySet())
            if (entry.getKey().matches(ip))
                return entry.getValue();
        return null;
    }
}
