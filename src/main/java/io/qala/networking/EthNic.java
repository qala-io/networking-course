package io.qala.networking;

import io.qala.networking.ipv4.ArpPacket;
import io.qala.networking.ipv4.IpAddress;
import io.qala.networking.ipv4.IpPacket;
import io.qala.networking.l2.Bridge;
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
        IpPacket ipPacket = new IpPacket(l2Packet);
        boolean targetedThisNic = ipPacket.dst().equals(this.address);
        if(ArpPacket.isArpReq(l2Packet))
            process(new ArpPacket(l2Packet));
        else if(targetedThisNic)
            kernel.process(ipPacket);
        else if(ipForward)
            kernel.route(ipPacket);
    }
    public void send(IpPacket ipPacket) {
    }
    public void process(ArpPacket req) {
        if(!req.dstIp().equals(address))
            if(promiscuous)
                kernel.process(req);
            else
                return;

    }
    public void setEndpoint(Endpoint<L2Packet> endpoint) {
        LOGGER.info("ip link set dev {} master {}", this, endpoint);
        this.endpoint = endpoint;
    }
    @Override public String toString() {
        return name;
    }
}
