package io.qala.networking.l2;

import io.qala.networking.Link;
import io.qala.networking.Router;
import io.qala.networking.ipv4.ArpPacket;

import java.util.ArrayList;
import java.util.List;

public class SpyBridge implements Router<L2Packet> {
    private final List<L2Packet> receivedReqs = new ArrayList<>();
    private final List<L2Packet> receivedReplies = new ArrayList<>();

    @Override public void route(Link<L2Packet> src, L2Packet packet) {
        if(ArpPacket.isArp(packet))
            if(new ArpPacket(packet).isReq())
                receivedReqs.add(packet);
            else
                receivedReplies.add(packet);
    }
    public List<L2Packet> getReceivedReqs() {
        return receivedReqs;
    }
    public List<L2Packet> getReceivedReplies() {
        return receivedReplies;
    }
}
