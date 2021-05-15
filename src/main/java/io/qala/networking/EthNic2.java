package io.qala.networking;

import io.qala.networking.l1.Cable;
import io.qala.networking.l2.L2Packet;
import io.qala.networking.l2.Mac;

public class EthNic2 implements Nic2 {
    private final Mac mac;
    private NetDevice dev;
    private boolean promiscuous;
    private Cable cable;

    public EthNic2() {
        this(Mac.random());
    }
    public EthNic2(Mac mac) {
        this.mac = mac;
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
    public void receive(Bytes l2Bytes) {
        L2Packet l2 = new L2Packet(l2Bytes, dev);
        l2.setToUs(l2.dst().equals(mac));
        if(l2.isToUs() || l2.dst().isBroadcast() || promiscuous)
            dev.receive(l2);
    }
    public void send(L2Packet l2) {
        cable.send(this, l2.toBytes());
    }
    @Override
    public Mac getMac() {
        return mac;
    }

    public void enterPromiscuousMode() {
        this.promiscuous = true;
    }
    public void attachToCable(Cable cable) {
        this.cable = cable;
    }

    public void connectLinuxDevice(NetDevice dev) {
        this.dev = dev;
    }

    @Override
    public String toString() {
        return "NIC(" + mac + ")";
    }
}
