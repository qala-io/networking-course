package io.qala.networking;

/**
 * TCP or Switch port
 */
public class Port {
    private final int num;

    public Port(int num) {
        this.num = num;
    }

    @Override public String toString() {
        return num + "";
    }
}
