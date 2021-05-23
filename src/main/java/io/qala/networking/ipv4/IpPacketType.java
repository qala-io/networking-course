package io.qala.networking.ipv4;

import io.qala.networking.TrafficStats;
import io.qala.networking.l2.L2Packet;

public class IpPacketType implements PacketType {
    private final RoutingTables rtables;
    private final TrafficStats trafficStats = new TrafficStats();

    public IpPacketType(RoutingTables rtables) {
        this.rtables = rtables;
    }

    /**
     * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/net/ipv4/ip_input.c#L531">ip_rcv</a>
     */
    public void rcv(L2Packet l2) {
        // now netfilter (iptables) is applied (PRE_ROUTING)
        if(!l2.isToUs())
            // we couldn't do this in Net Device because if the device had a master (bridge) - it's up to
            // the master how the packet should be processed
            return;
        IpPacket ip = new IpPacket(l2);
        Route rt = rtables.local().lookup(ip.dst());
        if(rt != null) {
            trafficStats.receivedPacket(l2.getDev(), ip);
            return;
        }
        rt = rtables.main().lookup(ip.dst());
        //need to forward the packet
    }

    @Override
    public boolean matches(L2Packet l2) {
        return IpPacket.isIp(l2);
    }

    /*
        App -(dst IP)-> Socket -> Kernel rtable -(src ip)-> Nic
         */
    public void sendArp() { }
}
