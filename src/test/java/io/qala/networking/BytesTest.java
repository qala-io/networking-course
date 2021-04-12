package io.qala.networking;

import org.junit.Test;

import static org.junit.Assert.*;

public class BytesTest {
    @Test public void canAcceptUnsignedBytesInIntForm() {
        assertArrayEquals(new byte[]{0, (byte) 255}, new Bytes(0, 255).getInternal());
    }
    @Test public void orsTwoByteArrays() {
        assertEquals(new Bytes(1, 3, 3), new Bytes(0, 1, 2).or(new Bytes(1, 2, 1)));
    }
    @Test public void andsTwoByteArrays() {
        assertEquals(new Bytes(0, 0, 2), new Bytes(0, 1, 2).and(new Bytes(1, 2, 6)));
    }
    @Test public void canGenerateIntFromBytes() {
        int integer = 0x01020304;
        Bytes bytes = new Bytes(1, 2, 3, 4);
        assertEquals(Integer.toHexString(integer), Integer.toHexString(bytes.toInt()));
        assertEquals(bytes, Bytes.fromInt(integer));
    }
}