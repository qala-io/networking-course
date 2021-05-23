package io.qala.networking.ipv4;

public class RoutingException extends RuntimeException {
    private final int returnCode;

    public RoutingException(String msg, int returnCode) {
        super(msg);
        this.returnCode = returnCode;
    }

    public static RoutingException networkUnreachable() {
        return new RoutingException("Network is unreachable", 2);
    }
    public int getReturnCode() {
        return returnCode;
    }
}
