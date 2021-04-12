package io.qala.networking;

import io.qala.networking.l2.L2Packet;
import io.qala.networking.l2.Mac;
import io.qala.networking.ipv4.IpAddress;

public class PacketFactory {
    /**
     *
     * @param src where the request is originating from
     * @param dstAddress address of the dst machine for which we want to get MAC address
     */
    public static L2Packet arpRequest(Mac src, IpAddress dstAddress) {
        return new L2Packet(src, Mac.BROADCAST, new Bytes(new byte[]{
                0, 1 /*ethernet*/,
                0, 1, //ARP Request - don't know exactly where this should be in the body and what's the mask
                dstAddress.get(0), dstAddress.get(1), dstAddress.get(2), dstAddress.get(3)
        }));
    }
}
