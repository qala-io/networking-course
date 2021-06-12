package io.qala.networking.ipv4;

/**
 * Just a collection of forwarding tables, see {@link FibTable}.
 */
public class FibTableList {
    private final FibTable local = new FibTable(RouteType.LOCAL);
    private final FibTable main = new FibTable(RouteType.REMOTE);

    public FibTable local() {
        return local;
    }
    public FibTable main() {
        return main;
    }

    public Route lookup(IpAddress ipAddress) {
        Route rt = local.lookup(ipAddress);
        if(rt == null)
            rt = main.lookup(ipAddress);
        return rt;
    }
}
