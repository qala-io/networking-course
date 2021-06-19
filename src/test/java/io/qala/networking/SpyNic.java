package io.qala.networking;

import io.qala.networking.l1.Cable;
import io.qala.networking.l1.NicMicrocontroller;
import io.qala.networking.l2.L2Packet;
import io.qala.networking.l2.Mac;

import java.util.ArrayList;
import java.util.List;

public class SpyNic extends NicMicrocontroller {
    public List<Bytes> receivedPackets = new ArrayList<>();

    @Override
    public void receive(Bytes packet) {
        receivedPackets.add(packet);
    }

    @Override
    public void send(Bytes l2) {

    }

    @Override
    public Mac getMac() {
        return null;
    }

    @Override
    public void attachToCable(Cable cable) {

    }
}
