package io.qala.networking.l1;

import io.qala.networking.Bytes;
import io.qala.networking.l2.L2Packet;
import io.qala.networking.l2.Mac;

public class NicMicrocontroller {
    private final Mac mac;
    private EthNicDriver driver;
    private Cable cable;
    private boolean promisc;

    public NicMicrocontroller() {
        this(Mac.random());
    }
    public NicMicrocontroller(Mac mac) {
        this.mac = mac;
    }

    public void attachToCable(Cable cable) {
        this.cable = cable;
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
        L2Packet l2 = new L2Packet(l2Bytes, null);
        l2.setToUs(l2.dst().equals(mac));
        if(l2.isToUs() || l2.dst().isBroadcast() || promisc)
            driver.receive(l2);
    }
    public void send(Bytes bytes) {
        cable.send(this, bytes);
    }

    public void enterPromiscMode() {
        this.promisc = true;
    }
    public void setDriver(EthNicDriver driver) {
        this.driver = driver;
    }

    public Mac getMac() {
        return mac;
    }
}
