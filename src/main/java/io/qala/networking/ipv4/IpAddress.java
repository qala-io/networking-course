package io.qala.networking.ipv4;

import io.qala.networking.Bytes;

public class IpAddress {
    private final int value;

    public IpAddress(String value) {
        String[] split = value.split("\\.");
        if(split.length != 4)
            throw new IllegalArgumentException("Invalid IPv4 address: " + value);
        byte[] bytes = new byte[4];
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            int next = Integer.parseInt(s);
            if (next > 255 || next < 0)
                throw new IllegalArgumentException("Invalid IPv4 address: " + value);
            bytes[i] = (byte) next;
        }
        this.value = new Bytes(bytes).toInt();
    }
    public IpAddress(byte[] value) {
        this(new Bytes(value));
    }
    public IpAddress(int value) {
        this.value = value;
    }
    public IpAddress(Bytes value) {
        this.value = value.toInt();
    }

    public byte get(int idx) {
        return toBytes().getInternal()[idx];
    }

    public int asInt() {
        return value;
    }
    public Bytes toBytes() {
        return Bytes.fromInt(value);
    }

    @Override public String toString() {
        StringBuilder s = new StringBuilder();
        for (byte b : toBytes().getInternal())
            s.append(Byte.toUnsignedInt(b)).append(".");
        s.deleteCharAt(s.length() - 1);
        return s.toString();
    }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IpAddress ipAddress = (IpAddress) o;
        return value == ipAddress.value;
    }
    @Override public int hashCode() {
        return value;
    }
}
