package io.qala.networking;

import io.qala.networking.l2.L2Packet;

public interface NetDevSender {
    void send(L2Packet l2);
}
