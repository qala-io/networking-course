package io.qala.networking;

import java.util.Arrays;
import java.util.Random;

public class Bytes {
    public static final byte FF = (byte) 0xff;
    private final byte[] bytes;

    /** Each int has to be smaller or equal to a byte. */
    public Bytes(int ... bytes) {
        this.bytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            this.bytes[i] = (byte) bytes[i];
        }
    }
    public Bytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public static byte[] highBits(int nOfBytes, int nOfOnes) {
        byte[] bytes = new byte[nOfBytes];
        for (int bit = 0; bit < nOfOnes; bit++)
            bytes[bit/8] |= 1 << (bit%8);
        return bytes;
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
    public byte[] getInternal() {
        return bytes;
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

    public static Bytes random(int n) {
        byte[] b = new byte[n];
        new Random().nextBytes(b);
        return new Bytes(b);
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
