package io.qala.networking.ipv4;

import io.qala.networking.Bytes;
import io.qala.networking.l2.L2Packet;
import io.qala.networking.l2.Mac;
import org.junit.Test;

import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class IpPacketTest {
    @Test public void canCreateL2PacketAndViceVersa() {
        IpPacket ip = new IpPacket(
                Mac.random(), IpAddress.random(),
                Mac.random(), IpAddress.random(),
                new Bytes(1, 128, 255));
        L2Packet l2 = ip.toL2();
        assertReflectionEquals(ip, new IpPacket(l2));
    }
}