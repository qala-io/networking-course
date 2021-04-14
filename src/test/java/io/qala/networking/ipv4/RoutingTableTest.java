package io.qala.networking.ipv4;

import io.qala.networking.l2.SpyNic;
import org.junit.Test;

import static org.junit.Assert.*;

public class RoutingTableTest {
    @Test public void returnsDefaultRouteIfNoOtherRoutesExist() {
        IpAddress defaultGateway = IpAddress.random();
        Routes routes = new Routes(defaultGateway, new SpyNic());

        Route route = routes.getRoute(IpAddress.random());
        assertSame(defaultGateway, route.getGateway());
    }
    @Test public void returnsDefaultRouteIfOthersDidNotMatch() {
        IpAddress defaultGateway = IpAddress.random();
        Routes routes = new Routes(defaultGateway, new SpyNic());
        routes.addLocalRoute(new NetworkId("192.168.0.0/24"), new SpyNic());
        routes.addGatewayRoute(new NetworkId("10.68.0.0/16"), new IpAddress("10.68.0.1"), new SpyNic());

        Route route = routes.getRoute(new IpAddress("10.67.0.1"));
        assertSame(defaultGateway, route.getGateway());
    }
    @Test public void returnsLocalGatewayIfIpMatchesDestination() {
        Routes routes = new Routes(IpAddress.random(), new SpyNic());
        NetworkId localDestination = new NetworkId("192.168.0.0/24");
        routes.addLocalRoute(localDestination, new SpyNic());
        routes.addGatewayRoute(new NetworkId("10.68.0.0/16"), new IpAddress("10.68.0.1"), new SpyNic());

        Route route = routes.getRoute(localDestination.randomAddr());
        assertTrue(route.isLocal());
    }
    @Test public void returnsRemoteGatewayIfIpMatchesDestination() {
        Routes routes = new Routes(IpAddress.random(), new SpyNic());
        NetworkId remoteDestination = new NetworkId("192.168.0.0/24");
        routes.addLocalRoute(new NetworkId("10.68.0.0/16"), new SpyNic());
        routes.addGatewayRoute(remoteDestination, new IpAddress("10.68.0.1"), new SpyNic());

        Route route = routes.getRoute(remoteDestination.randomAddr());
        assertEquals(new IpAddress("10.68.0.1"), route.getGateway());
    }
}