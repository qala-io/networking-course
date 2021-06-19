package io.qala.networking.ipv4;

import io.qala.networking.l2.L2Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/net/ipv4/arp.c#L1288">arp_packet_type</a>
 */
public class ArpPacketType implements PacketType {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArpPacketType.class);
    private final ArpTable arpTable;
    private final FibTableList rtable;
    private boolean ipForward;

    public ArpPacketType(ArpTable arpTable, FibTableList rtables) {
        this.arpTable = arpTable;
        this.rtable = rtables;
    }

    /**
     * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/net/ipv4/arp.c#L942">arp_rcv()</a>
     */
    @Override
    public void rcv(L2Packet l2) {
        ArpPacket arp = new ArpPacket(l2);
        LOGGER.info(l2.getDev() + " received " + arp.getType() + " from " + l2.src());
        // we'll accept ARP in either case:
        // local route: https://elixir.bootlin.com/linux/v5.12.1/source/net/ipv4/arp.c#L821
        // remote route: https://elixir.bootlin.com/linux/v5.12.1/source/net/ipv4/arp.c#L838
        Route rt = rtable.local().lookup(arp.dstIp());//https://elixir.bootlin.com/linux/v5.12.1/source/net/ipv4/arp.c#L818
        if(rt == null)
            rt = rtable.main().lookup(arp.dstIp());
        if(rt == null)
            return;
        arpTable.put(arp.srcIp(), arp.srcMac());
        //netfilter NF_ARP_IN
        if(arp.isReq()) // https://elixir.bootlin.com/linux/v5.12.1/source/net/ipv4/arp.c#L800
            if(rt.isLocal())
                // arp_send_dst() https://elixir.bootlin.com/linux/v5.12.1/source/net/ipv4/arp.c#L300
                // netfilter NF_ARP_OUT
                l2.getDev().send(arp.createReply(l2.getDev().getHardwareAddress()).toL2());
            else if(ipForward)
                rt.getDev().send(arp.toL2());
    }

    @Override
    public boolean matches(L2Packet l2) {
        return ArpPacket.isArp(l2);
    }

    public ArpTable getArpTable() {
        return arpTable;
    }
    public void enableIpForward() {
        ipForward = true;
    }
}
