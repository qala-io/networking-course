package io.qala.networking;

import io.qala.networking.l2.Bridge;
import io.qala.networking.l2.L2Packet;

public class BridgeSender implements NetDevSender {
    private final Bridge bridge;

    public BridgeSender(Bridge bridge) {
        this.bridge = bridge;
    }

    @Override public void send(L2Packet l2) {

    }
}
