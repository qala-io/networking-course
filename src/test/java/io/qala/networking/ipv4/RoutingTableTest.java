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

        Route route = routes.lookup(IpAddress.random());
        assertSame(dev, route.getDev());
        assertEquals(new IpRange("0.0.0.0/0"), route.getDestination());
    }
    @Test public void matchesExactlyIfRouteIsToHost() {
        NetDevice dev = dev();
        RoutingTable routes = new RoutingTable();
        routes.addRoute(new IpRange("10.12.12.1/32"), null, dev);

        Route route = routes.lookup(new IpAddress("10.12.12.1"));
        assertSame(dev, route.getDev());
    }
    @Test public void matchesBySubnetIfRouteIsForRange() {
        NetDevice dev = dev();
        RoutingTable routes = new RoutingTable();
        routes.addRoute(new IpRange("10.12.12.0/24"), null, dev);

        Route route = routes.lookup(new IpAddress("10.12.12.5"));
        assertSame(dev, route.getDev());
    }
    @Test public void ifMultipleRoutesMatch_chooseMostSpecific() {
        NetDevice dev = dev();
        RoutingTable routes = new RoutingTable();
        routes.addRoute(new IpRange("10.12.12.0/24"), null, dev);
        routes.addRoute(new IpRange("10.12.12.128/25"), null, dev);
        routes.addRoute(new IpRange("10.12.0.0/16"), null, dev);

        Route route = routes.lookup(new IpAddress("10.12.12.130"));
        assertEquals(new IpRange("10.12.12.128/25"), route.getDestination());
    }
    @Test public void nullIfNoRoutesMatch() {
        RoutingTable routes = new RoutingTable();
        callNoneOrMore(
                () -> routes.add(new Route(new IpRange("1.2.3.4/24"), null, dev(), RouteType.REMOTE)),
                () -> routes.add(new Route(new IpRange("10.30.50.120/8"), null, dev(), RouteType.LOCAL))
        );

        assertNull(routes.lookup(new IpAddress("11.67.0.1")));
    }

    private static NetDevice dev() {
        Host host = new Host();
        return host.net1.dev;
    }
}