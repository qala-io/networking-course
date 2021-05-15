package io.qala.networking;

import io.qala.networking.ipv4.ArpPacket;
import io.qala.networking.ipv4.IpAddress;
import io.qala.networking.ipv4.IpPacket;
import io.qala.networking.l2.L2Packet;
import io.qala.networking.l2.Mac;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.qala.datagen.RandomShortApi.numeric;

public class EthNic implements Nic {
    private static final Logger LOGGER = LoggerFactory.getLogger(EthNic.class);
    private final Mac mac;
    private boolean promiscuous;
    /**
     * {@code net.ipv4.ip_forward} prop which is written to {@code /proc/sys/net/ipv4/ip_forward}.
     */
    private boolean ipForward;
    private final Kernel kernel;
    private Router<L2Packet> link;//bridge or another NIC
    private final String name = "eth" + numeric(5);

    public EthNic(Kernel kernel) {
        this(Mac.random(), kernel);
    }
    public EthNic(Mac mac, Kernel kernel) {
        this.mac = mac;
        this.kernel = kernel;
    }
    @Override public void receive(Link<L2Packet> src, L2Packet l2Packet) {
        if(ArpPacket.isArp(l2Packet)) {
            ArpPacket arp = new ArpPacket(l2Packet);
            if(arp.isReq())
                process(arp);
            else
                kernel.putArpEntry(arp.srcIp(), arp.srcMac());
            return;
        }
        if(!IpPacket.isIp(l2Packet))
            throw new IllegalArgumentException("Unknown protocol");
        IpPacket ipPacket = new IpPacket(l2Packet);
        if(promiscuous || mac.equals(ipPacket.dstMac())) {
            LOGGER.info(this + " received IP packet from " + ipPacket.src());
            LogPadding.pad();
            kernel.process(this, ipPacket);
            LogPadding.unpad();
        }
    }
    public void sendArp(IpAddress srcIp, IpAddress ip) {
        LOGGER.info("Sending ARP for {}", ip);
        LogPadding.pad();
        link.route(this, ArpPacket.req(mac, srcIp, Mac.BROADCAST, ip).toL2());
        LogPadding.unpad();
    }
    @Override public void send(IpAddress srcIp, IpAddress dstIp, Mac dstMac, Bytes body) {
        LOGGER.info("Sending IP packet to {}", dstIp);
        LogPadding.pad();
        link.route(this, new IpPacket(mac, srcIp, dstMac, dstIp, body).toL2());
        LogPadding.unpad();
    }

    /**
     * P.13 https://www.cs.dartmouth.edu/~sergey/netreads/path-of-packet/Network_stack.pdf
     * netif_rx_schedule ->  __netif_rx_schedule() fills soft-net_data -> schedule NET_RX_SOFTIRQ -> net_rx_action() -> process_backlog() -> puts backlog_dev with function process_backlog() into queue
     * netif_rx() polls the queue
     *
     * <pre>
     * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/net/core/dev.c#L5376">__netif_receive_skb_one_core</a>:
     * determines packet type (pt_prev) using <a href="https://elixir.bootlin.com/linux/v5.12.1/source/net/core/dev.c#L5174">__netif_receive_skb_core</a>
     * and invoke the function of that packet type <a href="https://elixir.bootlin.com/linux/v5.12.1/source/net/ipv4/arp.c#L1290">pt_prev->func()</a> to handle the packet.
     * </pre>
     */
    private void process(ArpPacket arp) {
//        if(arp.isReq()) {
//            if(!arp.dstIp().equals(address)) {
//                if (promiscuous)
//                    kernel.process(arp);
//                return;
//            }
//            link.route(this, arp.createReply(this.mac).toL2());
//        }
//        else
//            kernel.putArpEntry(arp.srcIp(), arp.src());
    }
    public void setEndpoint(Router<L2Packet> link) {
        Loggers.TERMINAL_COMMANDS.info("ip link set dev {} master {}", this, link);
        this.link = link;
    }
    @Override public String toString() {
        return name;
    }
    Mac getMac() {
        return mac;
    }
    public IpAddress getIp() {
        return null;
    }
}
