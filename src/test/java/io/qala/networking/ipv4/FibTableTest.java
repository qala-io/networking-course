package io.qala.networking.ipv4;

import io.qala.networking.NetDevice;
import org.junit.Test;

import static io.qala.datagen.RandomShortApi.callNoneOrMore;
import static org.junit.Assert.*;

public class FibTableTest {
    @Test public void returnsDefaultRouteIfNoOtherRoutesExist() {
        NetDevice dev = dev();
        FibTable routes = new FibTable(RouteType.LOCAL);
        routes.addDefaultRoute(dev);

        Route route = routes.lookup(IpAddress.random());
        assertSame(dev, route.getDev());
        assertEquals(new IpRange("0.0.0.0/0"), route.getDestination());
    }
    @Test public void matchesExactlyIfRouteIsToHost() {
        NetDevice dev = dev();
        FibTable routes = new FibTable(RouteType.LOCAL);
        routes.addRoute(new IpRange("10.12.12.1/32"), null, dev);

        Route route = routes.lookup(new IpAddress("10.12.12.1"));
        assertSame(dev, route.getDev());
    }
    @Test public void matchesBySubnetIfRouteIsForRange() {
        NetDevice dev = dev();
        FibTable routes = new FibTable(RouteType.LOCAL);
        routes.addRoute(new IpRange("10.12.12.0/24"), null, dev);

        Route route = routes.lookup(new IpAddress("10.12.12.5"));
        assertSame(dev, route.getDev());
    }
    @Test public void ifMultipleRoutesMatch_chooseMostSpecific() {
        NetDevice dev = dev();
        FibTable routes = new FibTable(RouteType.LOCAL);
        routes.addRoute(new IpRange("10.12.12.0/24"), null, dev);
        routes.addRoute(new IpRange("10.12.12.128/25"), null, dev);
        routes.addRoute(new IpRange("10.12.0.0/16"), null, dev);

        Route route = routes.lookup(new IpAddress("10.12.12.130"));
        assertEquals(new IpRange("10.12.12.128/25"), route.getDestination());
    }
    @Test public void nullIfNoRoutesMatch() {
        FibTable routes = new FibTable(RouteType.LOCAL);
        callNoneOrMore(
                () -> routes.add(new Route(new IpRange("1.2.3.0/24"), null, dev(), RouteType.REMOTE)),
                () -> routes.add(new Route(new IpRange("10.0.0.0/8"), null, dev(), RouteType.LOCAL))
        );

        assertNull(routes.lookup(new IpAddress("11.67.0.1")));
    }

    private static NetDevice dev() {
        Host host = new Host();
        return host.dev1.dev;
    }
}