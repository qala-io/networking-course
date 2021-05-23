package io.qala.networking.ipv4;

import io.qala.networking.NetDevice;

public class Route {
    /**
     * Either a specific address of a host (e.g. for local routes) or a range with a network mask. If it's
     * a single host address, {@code route} command will show "H" in flags for this route.
     */
    private final IpRange destination;
    /**
     * In case the route points to the gateway ("via" flag). If it's present, then {@code route} command
     * would show "G" (for Gateway) in the flags.
     */
    private final IpAddress nextHop;
    /**
     * "dev" flag. When adding a route using {@code ip route add x.x.x.x/x via y.y.y.y} and "dev" isn't specified
     * explicitly, Linux first looks up the route for {@code y.y.y.y} and discovers which device to use.
     */
    private final NetDevice dev;
    private final RouteType type;

    public Route(IpRange destination, IpAddress nextHop, NetDevice dev, RouteType routeType) {
        this.destination = destination;
        this.nextHop = nextHop;
        this.dev = dev;
        this.type = routeType;
    }
    public boolean isLocal() {
        return type == RouteType.LOCAL;
    }
    public NetDevice getDev() {
        return dev;
    }
    public IpAddress getNextHop() {
        return nextHop;
    }
    public IpRange getDestination() {
        return destination;
    }
    public IpAddress getNextHopFor(IpAddress ip) {
        if(!destination.matches(ip))
            throw new IllegalArgumentException(ip + " doesn't match destination " + destination);
        if(isLocal())
            return ip;
        return nextHop;
    }
}
