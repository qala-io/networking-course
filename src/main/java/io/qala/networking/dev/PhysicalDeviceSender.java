package io.qala.networking.dev;

import io.qala.networking.l1.NicDriver;
import io.qala.networking.l2.L2Packet;

public class PhysicalDeviceSender implements NetDevSender {
    private final NicDriver nic;

    public PhysicalDeviceSender(NicDriver nic) {
        this.nic = nic;
    }
    @Override public void send(L2Packet l2) {
        nic.send(l2);
    }
}
