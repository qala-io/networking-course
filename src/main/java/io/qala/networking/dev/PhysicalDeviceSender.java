package io.qala.networking.dev;

import io.qala.networking.l1.EthNicDriver;
import io.qala.networking.l2.L2Packet;

public class PhysicalDeviceSender implements NetDevSender {
    private final EthNicDriver nic;

    public PhysicalDeviceSender(EthNicDriver nic) {
        this.nic = nic;
    }
    @Override public void send(L2Packet l2) {
        nic.send(l2);
    }
}
