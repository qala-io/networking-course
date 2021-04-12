package io.qala.networking.ipv4;

import io.qala.networking.Bytes;

public class IpAddress {
    private final Bytes value;

    public IpAddress(String value) {
        String[] split = value.split("\\.");
        if(split.length != 4)
            throw new IllegalArgumentException("Invalid IPv4 address: " + value);
        byte[] result = new byte[4];
        for (int i = 0; i < split.length; i++) {
            int next = Integer.parseInt(split[i]);
            if(next > 255 || next < 0)
                throw new IllegalArgumentException("Invalid IPv4 address: " + value);
            result[i] = (byte) next;
        }
        this.value = new Bytes(result);
    }
    public IpAddress(byte[] value) {
        this(new Bytes(value));
    }
    public IpAddress(Bytes value) {
        this.value = value;
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
            s.append(Byte.toUnsignedInt(b)).append(".");
        s.deleteCharAt(s.length() - 1);
        return s.toString();
    }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IpAddress ipAddress = (IpAddress) o;
        return value.equals(ipAddress.value);
    }
    @Override public int hashCode() {
        return value.hashCode();
    }
}
