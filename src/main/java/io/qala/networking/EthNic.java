package io.qala.networking;

import io.qala.networking.ipv4.ArpPacket;
import io.qala.networking.ipv4.IpAddress;
import io.qala.networking.ipv4.IpPacket;
import io.qala.networking.l2.L2Packet;
import io.qala.networking.l2.Mac;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.qala.datagen.RandomShortApi.alphanumeric;

public class EthNic implements Nic {
    private static final Logger LOGGER = LoggerFactory.getLogger(EthNic.class);
    private final Mac mac;
    private final IpAddress address;
    private boolean promiscuous;
    /**
     * {@code net.ipv4.ip_forward} prop which is written to {@code /proc/sys/net/ipv4/ip_forward}.
     */
    private boolean ipForward;
    private final Kernel kernel;
    private Endpoint<L2Packet> endpoint;//bridge or another NIC
    private final String name = "eth-" + alphanumeric(5);

    public EthNic(Mac mac, IpAddress address, Kernel kernel) {
        this.mac = mac;
        this.address = address;
        this.kernel = kernel;
    }
    @Override public void process(Endpoint<L2Packet> src, L2Packet l2Packet) {
        if(ArpPacket.isArp(l2Packet)) {
            ArpPacket arp = new ArpPacket(l2Packet);
            if(arp.isReq())
                process(src, arp);
            else
                kernel.putArpEntry(arp.srcIp(), arp.src());
            return;
        }
        IpPacket ipPacket = new IpPacket(l2Packet);
        boolean targetedThisNic = ipPacket.dst().equals(this.address);
        if(IpPacket.isIp(l2Packet))
            kernel.process(ipPacket);
        else if(ipForward)
            kernel.route(ipPacket);
    }
    public void send(IpPacket ipPacket) {
    }

    public void process(Endpoint<L2Packet> src, ArpPacket req) {
        if(!req.dstIp().equals(address))
            if(promiscuous)
                kernel.process(req);
            else
                return;
        src.process(this, req.createReply(this.mac).toL2());
    }
    public void setEndpoint(Endpoint<L2Packet> endpoint) {
        LOGGER.info("ip link set dev {} master {}", this, endpoint);
        this.endpoint = endpoint;
    }
    @Override public String toString() {
        return name;
    }
}
