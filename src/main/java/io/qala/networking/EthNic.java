package io.qala.networking;

import io.qala.networking.l2.L2Packet;
import io.qala.networking.ipv4.IpAddress;
import io.qala.networking.ipv4.IpPacket;

public class EthNic implements Nic {
    private final IpAddress address;
    /**
     * {@code net.ipv4.ip_forward} prop which is written to {@code /proc/sys/net/ipv4/ip_forward}.
     */
    private boolean ipForward;
    private final Kernel kernel;

    public EthNic(IpAddress address, Kernel kernel) {
        this.address = address;
        this.kernel = kernel;
    }
    @Override public void process(L2Packet l2Packet) {
        IpPacket ipPacket = new IpPacket(l2Packet);
        boolean targetedThisNic = ipPacket.dst().equals(this.address);
        if(targetedThisNic && ipPacket.isArp())// ARP is not an IP packet?
            throw new UnsupportedOperationException();
//            send(new IpPacket(null));
        else if(targetedThisNic)
            kernel.process(ipPacket);
        else if(ipForward)
            kernel.route(ipPacket);
    }
    public void send(IpPacket ipPacket) {

    }
}
