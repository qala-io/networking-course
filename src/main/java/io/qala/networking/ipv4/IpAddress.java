package io.qala.networking.ipv4;

import io.qala.networking.Bytes;

import java.nio.charset.StandardCharsets;

public class IpAddress {
    private final Bytes value;

    public IpAddress(String value) {
        this(value.getBytes(StandardCharsets.UTF_8));//need to rework this funny implementation
    }
    public IpAddress(byte[] value) {
        this.value = new Bytes(value);
    }

    public byte get(int idx) {
        return value.getInternal()[idx];
    }

    public Bytes toBytes() {
        return value;
    }

    @Override public String toString() {
        StringBuilder s = new StringBuilder();
        for (byte b : value.getInternal())
            s.append(Integer.toString(b)).append(".");
        s.deleteCharAt(s.length() - 1);
        return s.toString();
    }
}
