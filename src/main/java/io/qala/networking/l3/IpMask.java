package io.qala.networking.l3;

public class IpMask {
    private final IpAddress mask;
    public IpMask(IpAddress mask) {
        this.mask = mask;
    }
    public boolean matches(IpAddress address) {
        throw new UnsupportedOperationException();
    }
}
