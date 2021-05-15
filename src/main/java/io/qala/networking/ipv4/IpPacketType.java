package io.qala.networking.ipv4;

import io.qala.networking.Nic;
import io.qala.networking.TrafficStats;
import io.qala.networking.l2.L2Packet;

import java.util.HashMap;

public class IpPacketType implements PacketType {
    private final Routes rtable = new Routes();
    private final TrafficStats trafficStats = new TrafficStats();
    private final HashMap<Nic, IpAddress> nicIpAddress = new HashMap<>();
    private final ArpTable arpTable = new ArpTable();

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
        //lookup the route: is it for us or should we forward?
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
