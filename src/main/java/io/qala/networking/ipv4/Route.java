package io.qala.networking.ipv4;

import io.qala.networking.NetDevice;

public class Route {
    private final Cidr destination;
    private final IpAddress gateway;
    private final NetDevice dev;
    private final RouteType type;

    public Route(Cidr destination, IpAddress gateway, NetDevice dev, RouteType routeType) {
        this.destination = destination;
        this.gateway = gateway;
        this.dev = dev;
        this.type = routeType;
    }
    public boolean isLocal() {
        return gateway.equals(IpAddress.MIN);
    }
    public NetDevice getDev() {
        return dev;
    }
    public IpAddress getGateway() {
        return gateway;
    }
    public IpAddress getNextHopFor(IpAddress ip) {
        if(!destination.matches(ip))
            throw new IllegalArgumentException(ip + " doesn't match destination " + destination);
        if(isLocal())
            return ip;
        return gateway;
    }
}
