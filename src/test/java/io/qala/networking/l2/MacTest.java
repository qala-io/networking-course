package io.qala.networking.l2;

import org.junit.Test;

import static io.qala.networking.Bytes.FF;
import static org.junit.Assert.*;

public class MacTest {
    @Test public void convertsToBytesAndBack() {
        Mac mac = new Mac(FF, (byte) 0xA1, (byte) 1, FF, (byte) 2, (byte) 0x3B);
        assertEquals("ff:a1:01:ff:02:3b", mac.toString());
        assertEquals(mac, new Mac(mac.toString()));
    }
}