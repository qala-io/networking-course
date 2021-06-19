package io.qala.networking.dev;

import io.qala.networking.l2.Bridge;
import io.qala.networking.l2.L2Packet;

public class BridgeSender implements NetDevSender {
    private Bridge bridge;

    @Override public void send(L2Packet l2) {
        NetDevice to = bridge.getInterface(l2.dst());
        l2.setDev(to);
        to.send(l2);
    }

    public void setBridge(Bridge bridge) {
        this.bridge = bridge;
    }
}
