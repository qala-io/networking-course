package io.qala.networking.ipv4;

import io.qala.networking.l2.L2Packet;

/**
 * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/net/ipv4/arp.c#L1288">arp_packet_type</a>
 */
public class ArpPacketType implements PacketType {
    private final ArpTable arpTable;

    public ArpPacketType() {
        this(new ArpTable());
    }
    public ArpPacketType(ArpTable arpTable) {
        this.arpTable = arpTable;
    }

    /**
     * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/net/ipv4/arp.c#L942">arp_rcv()</a>
     */
    @Override
    public void rcv(L2Packet l2) {
        ArpPacket arp = new ArpPacket(l2);
        if(!l2.getDev().matches(arp.dstIp())) //https://elixir.bootlin.com/linux/v5.12.1/source/net/ipv4/arp.c#L824
            return;
        arpTable.put(arp.srcIp(), arp.srcMac());
        //netfilter NF_ARP_IN
        if(arp.isReq()) // https://elixir.bootlin.com/linux/v5.12.1/source/net/ipv4/arp.c#L800
            // arp_send_dst() https://elixir.bootlin.com/linux/v5.12.1/source/net/ipv4/arp.c#L300
            // netfilter NF_ARP_OUT
            l2.getDev().send(arp.createReply(l2.getDev().getMac()).toL2());
    }

    @Override
    public boolean matches(L2Packet l2) {
        return ArpPacket.isArp(l2);
    }

    public ArpTable getArpTable() {
        return arpTable;
    }
}
