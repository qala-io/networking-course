package io.qala.networking;

import io.qala.networking.ipv4.IpAddress;
import io.qala.networking.l2.L2Packet;
import io.qala.networking.l2.Mac;

/**
 * Network Interface Controller (aka Network Card, Network Adapter)
 */
public interface Nic extends Link<L2Packet> {
    void setEndpoint(Link<L2Packet> link);
    void sendArp(IpAddress dst);
    void send(IpAddress dstIp, Mac dstMac, Bytes body);
}