package io.qala.networking;

import java.util.Arrays;
import java.util.Random;

public class Bytes {
    public static final byte FF = (byte) 0xff;
    private final byte[] bytes;

    /**
     * @param bytes unsigned bytes within an integer. Each must be between 0 and 255.
     */
    public Bytes(int ... bytes) {
        this.bytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            int integer = bytes[i];
            if(integer > 255 || integer < 0)
                throw new IllegalArgumentException("Not an unsigned byte: " + integer);
            this.bytes[i] = (byte) integer;
        }
    }
    public Bytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public Bytes append(Bytes bytes) {
        return append(bytes.getInternal());
    }
    public Bytes append(byte ... toAppend) {
        byte[] newBytes = new byte[toAppend.length + this.bytes.length];
        System.arraycopy(this.bytes, 0, newBytes, 0, this.bytes.length);
        System.arraycopy(toAppend, 0, newBytes, this.bytes.length, toAppend.length);
        return new Bytes(newBytes);
    }
    public Bytes get(int from, int toExclusive) {
        byte[] result = new byte[toExclusive - from];
        System.arraycopy(bytes, from, result, 0, result.length);
        return new Bytes(result);
    }
    public byte get(int idx) {
        return bytes[idx];
    }
    public byte[] getInternal() {
        return bytes;
    }
    public Bytes or(Bytes o) {
        if(o.size() != size())
            throw new IllegalArgumentException("Bytes are of different lengths: " + size() + " and " + o.size());
        byte[] result = new byte[size()];
        for (int i = 0; i < bytes.length; i++)
            result[i] = (byte) (bytes[i] | o.bytes[i]);
        return new Bytes(result);
    }
    public Bytes and(Bytes o) {
        if(o.size() != size())
            throw new IllegalArgumentException("Bytes are of different lengths: " + size() + " and " + o.size());
        byte[] result = new byte[size()];
        for (int i = 0; i < bytes.length; i++)
            result[i] = (byte) (bytes[i] & o.bytes[i]);
        return new Bytes(result);
    }

    public int size() {
        return bytes.length;
    }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bytes bytes1 = (Bytes) o;
        return Arrays.equals(bytes, bytes1.bytes);
    }
    @Override public int hashCode() {
        return Arrays.hashCode(bytes);
    }
    @Override public String toString() {
        return "[" + Arrays.toString(bytes) + ']';
    }
    public static Bytes random(int n) {
        byte[] b = new byte[n];
        new Random().nextBytes(b);
        return new Bytes(b);
    }
    public boolean startsWith(Bytes beginning) {
        for (int i = 0; i < beginning.bytes.length; i++)
            if (beginning.getInternal()[i] != getInternal()[i])
                return false;
        return true;
    }
    public int toInt() {
        if(bytes.length != 4)
            throw new IllegalStateException("Int requires 4 bytes, actual: " + this);
        int result = 0;
        for (byte next : bytes) {
            result <<= 8;
            result |= Byte.toUnsignedInt(next);
        }
        return result;
    }
    public static Bytes fromInt(int v) {
        byte[] bytes = new byte[4];
        for (int i = bytes.length - 1; i >= 0; i--) {
            bytes[i] = (byte) (v & 0xff);
            v >>>= 8;
        }
        return new Bytes(bytes);
    }

    public static String toHex(byte b) {
        String s = Integer.toHexString(Byte.toUnsignedInt(b));
        if(s.length() == 1)
            s = "0" + s;
        return s;
    }
    public static byte toByte(String hexByte) {
        return (byte) Integer.parseInt(hexByte, 16);
    }
}
