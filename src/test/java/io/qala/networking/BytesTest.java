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
    @Test public void generatesBitsWithHigh1() {
        assertEquals(new Bytes(Bytes.FF, 128), Bytes.highBits(2, 9));
    }
}