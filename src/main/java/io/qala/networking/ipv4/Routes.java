package io.qala.networking.ipv4;

import io.qala.networking.Loggers;
import io.qala.networking.Nic;

import java.util.HashMap;
import java.util.Map;

public class Routes {
    private Route defaultGateway;
    private final Map<NetworkId, Route> table = new HashMap<>();
    public Routes() { }
    public Routes(IpAddress defaultGateway, Nic device) {
         addDefaultRoute(defaultGateway, device);
    }
    public void addDefaultRoute(IpAddress gateway, Nic nic) {
        Loggers.TERMINAL_COMMANDS.info("ip route add default via {} dev {}", gateway, nic);
        NetworkId defaultDestination = new NetworkId(IpAddress.MIN, IpAddress.MAX);
        this.defaultGateway = new Route(defaultDestination, gateway, nic);
    }
    public void addLocalRoute(NetworkId destination, Nic nic) {
        Loggers.TERMINAL_COMMANDS.info("ip route add to local {} dev {}", destination, nic);
        table.put(destination, new Route(destination, IpAddress.MIN, nic));
    }
    public void addGatewayRoute(NetworkId destination, IpAddress gateway, Nic nic) {
        Loggers.TERMINAL_COMMANDS.info("ip route add {} via {} dev {}", destination, gateway, nic);
        table.put(destination, new Route(destination, gateway, nic));
    }
    public Route getRoute(IpAddress ip ) {
        for (Map.Entry<NetworkId, Route> entry : table.entrySet())
            if (entry.getKey().matches(ip))
                return entry.getValue();
        return defaultGateway;
    }
}
