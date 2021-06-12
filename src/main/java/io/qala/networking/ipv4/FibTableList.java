package io.qala.networking.ipv4;

/**
 * Just a collection of forwarding tables, see {@link FibTable}.
 */
public class FibTableList {
    private final FibTable local = new FibTable();
    private final FibTable main = new FibTable();

    public FibTable local() {
        return local;
    }
    public FibTable main() {
        return main;
    }
}
