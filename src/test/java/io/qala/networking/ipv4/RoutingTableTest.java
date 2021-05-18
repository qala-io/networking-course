package io.qala.networking.ipv4;

import io.qala.networking.NetDevice;
import org.junit.Test;

import static org.junit.Assert.*;

public class RoutingTableTest {
    @Test public void returnsDefaultRouteIfNoOtherRoutesExist() {
        IpAddress defaultGateway = IpAddress.random();
        RoutingTable routes = new RoutingTable(defaultGateway, dev());

        Route route = routes.getRoute(IpAddress.random());
        assertSame(defaultGateway, route.getGateway());
    }
    @Test public void returnsDefaultRouteIfOthersDidNotMatch() {
        IpAddress defaultGateway = IpAddress.random();
        RoutingTable routes = new RoutingTable(defaultGateway, dev());
        routes.addLocalRoute(new NetworkId("192.168.0.0/24"), dev());
        routes.addGatewayRoute(new NetworkId("10.68.0.0/16"), new IpAddress("10.68.0.1"), dev());

        Route route = routes.getRoute(new IpAddress("10.67.0.1"));
        assertSame(defaultGateway, route.getGateway());
    }
    @Test public void returnsLocalGatewayIfIpMatchesDestination() {
        RoutingTable routes = new RoutingTable(IpAddress.random(), dev());
        NetworkId localDestination = new NetworkId("192.168.0.0/24");
        routes.addLocalRoute(localDestination, dev());
        routes.addGatewayRoute(new NetworkId("10.68.0.0/16"), new IpAddress("10.68.0.1"), dev());

        Route route = routes.getRoute(localDestination.randomAddr());
        assertTrue(route.isLocal());
    }
    @Test public void returnsRemoteGatewayIfIpMatchesDestination() {
        RoutingTable routes = new RoutingTable(IpAddress.random(), dev());
        NetworkId remoteDestination = new NetworkId("192.168.0.0/24");
        routes.addLocalRoute(new NetworkId("10.68.0.0/16"), dev());
        routes.addGatewayRoute(remoteDestination, new IpAddress("10.68.0.1"), dev());

        Route route = routes.getRoute(remoteDestination.randomAddr());
        assertEquals(new IpAddress("10.68.0.1"), route.getGateway());
    }

    private static NetDevice dev() {
        ArpPacketTypeTest.Host host = new ArpPacketTypeTest.Host();
        return host.net1.dev;
    }
}