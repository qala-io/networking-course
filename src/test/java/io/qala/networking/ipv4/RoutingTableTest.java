package io.qala.networking.ipv4;

import io.qala.networking.NetDevice;
import org.junit.Test;

import static io.qala.datagen.RandomShortApi.callNoneOrMore;
import static org.junit.Assert.*;

public class RoutingTableTest {
    @Test public void returnsDefaultRouteIfNoOtherRoutesExist() {
        NetDevice dev = dev();
        RoutingTable routes = new RoutingTable();
        routes.addDefaultRoute(dev);

        Route route = routes.getRoute(IpAddress.random());
        assertSame(dev, route.getDev());
        assertEquals(new IpRange("0.0.0.0/0"), route.getDestination());
    }
    @Test public void nullIfNoRoutesMatch() {
        RoutingTable routes = new RoutingTable();
        callNoneOrMore(
                () -> routes.add(new Route(new IpRange("1.2.3.4/24"), null, dev(), RouteType.REMOTE)),
                () -> routes.add(new Route(new IpRange("10.30.50.120/8"), null, dev(), RouteType.LOCAL))
        );

        assertNull(routes.getRoute(new IpAddress("11.67.0.1")));
    }

    private static NetDevice dev() {
        Host host = new Host();
        return host.net1.dev;
    }
}