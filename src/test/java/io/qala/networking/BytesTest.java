package io.qala.networking;

import org.junit.Test;

import static org.junit.Assert.*;

public class BytesTest {
    @Test public void canAcceptUnsignedBytesInIntForm() {
        assertArrayEquals(new byte[]{0, (byte) 255}, new Bytes(0, 255).getInternal());
    }
}