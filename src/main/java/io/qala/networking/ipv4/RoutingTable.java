package io.qala.networking.ipv4;

import io.qala.networking.Loggers;
import io.qala.networking.NetDevice;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class RoutingTable {
    private static final IpRange DEFAULT_ROUTE = new IpRange("0.0.0.0/0");
    private final Map<IpRange, Route> rangeRoutes = new TreeMap<>(
            // The most specific route (with most bits in mask) should come first to have higher priority.
            // In real Kernel though there are more rules to resolve ties
            Comparator.comparingInt(IpRange::getNetworkBitCount).reversed()
    );
    private final Map<IpAddress, Route> singleHostRoutes = new HashMap<>();
    public RoutingTable() { }

    public void add(Route route) {// see other methods for the right terminal commands of iproute2 package
        if(!route.getDestination().matches(route.getDestination().getAddress()))
            throw new RoutingException("Route destination address doesn't match its own IP Address." +
                                       " Does network address match network prefix? Maybe too many " +
                                       "network bits are specified?", 0);
        if(route.getDestination().getNetworkBitCount() == 32)
            singleHostRoutes.put(route.getDestination().getAddress(), route);
        else
            rangeRoutes.put(route.getDestination(), route);
    }

    public void addDefaultRoute(NetDevice dev) {
        Loggers.TERMINAL_COMMANDS.info("ip route add default dev {}", dev);
        add(new Route(DEFAULT_ROUTE, null, dev, RouteType.LOCAL));
    }
    public void addDefaultRoute(IpAddress gateway, NetDevice dev) {
        Loggers.TERMINAL_COMMANDS.info("ip route add default via {} dev {}", gateway, dev);
        add(new Route(DEFAULT_ROUTE, gateway, dev, RouteType.REMOTE/*??*/));
    }
    public void addRoute(IpRange destination, IpAddress gateway, NetDevice dev) {
        Loggers.TERMINAL_COMMANDS.info("ip route add {} via {} dev {}", destination, gateway, dev);
        add(new Route(destination, gateway, dev, RouteType.REMOTE/*??*/));
    }
    /**
     * Real route lookup is more sophisticated:
     * <ol>
     *     <li>It may use more data about the routes to find which one is suited better when multiple routes match
     *     the address</li>
     *     <li>Also more parameters about the requests & devices can be used with Policy Routing</li>
     *     <li>An external route may have multiple gateways configured and it's possible to configure which of the
     *     gateways to use and when</li>
     * </ol>
     */
    public Route lookup(IpAddress ip) {
        Route route = singleHostRoutes.get(ip);
        if(route != null)
            return route;
        for (Map.Entry<IpRange, Route> entry : rangeRoutes.entrySet())
            if (entry.getKey().matches(ip))
                return entry.getValue();
        return null;
    }
}
