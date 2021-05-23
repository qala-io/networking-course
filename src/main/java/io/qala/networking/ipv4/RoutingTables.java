package io.qala.networking.ipv4;

public class RoutingTables {
    private final RoutingTable local = new RoutingTable();
    private final RoutingTable main = new RoutingTable();

    public RoutingTable local() {
        return local;
    }
    public RoutingTable main() {
        return main;
    }
}
