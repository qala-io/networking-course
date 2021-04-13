package io.qala.networking.l2;

import io.qala.networking.Bytes;
import io.qala.networking.Link;
import io.qala.networking.Nic;
import io.qala.networking.Router;
import io.qala.networking.ipv4.ArpPacket;
import io.qala.networking.ipv4.IpAddress;

import java.util.ArrayList;
import java.util.List;

public class SpyNic implements Nic {
    private final List<L2Packet> receivedReqs = new ArrayList<>();
    private final List<L2Packet> receivedReplies = new ArrayList<>();

    @Override public void receive(Link<L2Packet> src, L2Packet packet) {
        if(ArpPacket.isArp(packet))
            if(new ArpPacket(packet).isReq())
                receivedReqs.add(packet);
            else
                receivedReplies.add(packet);
    }
    @Override public void setEndpoint(Router<L2Packet> link) {

    }
    @Override public void sendArp(IpAddress dst) {

    }
    @Override public void send(IpAddress dstIp, Mac dstMac, Bytes body) {

    }
    public int receivedArpRequests() {
        return receivedReqs.size();
    }
    public int receivedArpReplies() {
        return receivedReplies.size();
    }
    public L2Packet get(int idx) {
        return receivedReqs.get(idx);
    }
}
