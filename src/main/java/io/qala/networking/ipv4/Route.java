package io.qala.networking.ipv4;

import io.qala.networking.Nic;

public class Route {
    private final NetworkId destination;
    private final IpAddress gateway;
    private final Nic nic;

    public Route(NetworkId destination, IpAddress gateway, Nic nic) {
        this.destination = destination;
        this.gateway = gateway;
        this.nic = nic;
    }
    public boolean isLocal() {
        return gateway.equals(IpAddress.MIN);
    }
    public Nic getNic() {
        return nic;
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
