package io.qala.networking;

import io.qala.networking.l2.L2Packet;

/**
 * Network Interface Controller (aka Network Card, Network Adapter)
 */
public interface Nic {
    void process(L2Packet packet);
}
