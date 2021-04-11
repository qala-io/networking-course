package io.qala.networking.l3;

import java.nio.charset.StandardCharsets;

public class IpAddress {
    private final byte[] value;

    public IpAddress(String value) {
        this(value.getBytes(StandardCharsets.UTF_8));//need to rework this funny implementation
    }
    public IpAddress(byte[] value) {
        this.value = value;
    }

    public byte get(int idx) {
        return value[idx];
    }
}
