package io.qala.networking;

import io.qala.networking.l2.L2Packet;

public interface L1Endpoint {
    void receive(L2Packet packet);
}
