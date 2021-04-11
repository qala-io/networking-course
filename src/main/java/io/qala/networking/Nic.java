package io.qala.networking;

import io.qala.networking.l2.L2Packet;

interface Nic {
    void process(L2Packet packet);
}
