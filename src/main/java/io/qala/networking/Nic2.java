package io.qala.networking;

import io.qala.networking.l1.Cable;
import io.qala.networking.l2.L2Packet;
import io.qala.networking.l2.Mac;

public interface Nic2 {
    void receive(Bytes l2Bytes);
    void send(L2Packet l2);
    Mac getMac();
    void connectLinuxDevice(NetDevice dev);

    void attachToCable(Cable cable);
}
