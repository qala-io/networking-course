package io.qala.networking.ipv4;

import io.qala.networking.l2.Mac;
import org.junit.Test;

import static io.qala.datagen.RandomShortApi.sample;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class ArpPacketTest {
    @Test public void canConvertToAndFromBytes() {
        Cidr network = Cidr.random();
        ArpPacket sent = new ArpPacket(
                sample(ArpPacket.Type.values()),
                Mac.random(), Mac.random(),
                network.randomAddr(), network.randomAddr());
        ArpPacket received = new ArpPacket(sent.toL2());
        assertReflectionEquals(sent, received);
    }
}