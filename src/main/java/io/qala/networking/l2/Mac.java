package io.qala.networking.l2;

import io.qala.networking.Bytes;

import static io.qala.networking.Bytes.FF;

public class Mac {
    public static final Mac BROADCAST = new Mac(FF, FF, FF, FF, FF, FF),
                            EMPTY = new Mac(new Bytes(0, 0, 0, 0, 0, 0));
    private final Bytes value;

    Mac(Bytes value) {
        this.value = value;
    }

    Mac(byte... value) {
        this.value = new Bytes(value);
    }
    Mac(String s) {
        if(s == null || s.length() != 17)
            throw new IllegalArgumentException("Invalid MAC address: " + s);
        String[] bytes = s.split(":");
        if(bytes.length != 6)
            throw new IllegalArgumentException("Invalid MAC address: " + s);
        byte[] value = new byte[6];
        for (int i = 0; i < bytes.length; i++)
            value[i] = Bytes.toByte(bytes[i]);
        this.value = new Bytes(value);
    }

    public boolean isBroadcast() {
        return BROADCAST.equals(this);
    }

    public Bytes toBytes() {
        return value;
    }
    @Override public String toString() {
        StringBuilder hex = new StringBuilder();
        for (byte b : value.getInternal())
            hex.append(Bytes.toHex(b)).append(":");
        hex.deleteCharAt(hex.length() - 1);
        return hex.toString().toLowerCase();
    }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mac mac = (Mac) o;
        return value.equals(mac.toBytes());
    }
    @Override public int hashCode() {
        return value.hashCode();
    }

    public static Mac random() {
        return new Mac(Bytes.random(6));
    }
}
