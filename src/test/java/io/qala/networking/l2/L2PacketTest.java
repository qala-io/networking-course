package io.qala.networking.l2;

import io.qala.networking.dev.NetDevice;
import io.qala.networking.ipv4.ArpPacket;
import io.qala.networking.ipv4.IpAddress;
import org.junit.Test;

import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class L2PacketTest {
    @Test public void copyingConstructorCreatesAnIdenticalCopy() {
        L2Packet l2 = new L2Packet(
                ArpPacket.req(Mac.random(), IpAddress.random(), Mac.random(), IpAddress.random()).toL2().toBytes(),
                new NetDevice(null, null, null)
        );
        l2.setToUs(true);
        L2Packet copy = new L2Packet(l2);
        assertReflectionEquals(l2, copy);
    }

}