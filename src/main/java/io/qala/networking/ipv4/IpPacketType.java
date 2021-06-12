package io.qala.networking.ipv4;

import io.qala.networking.Bytes;
import io.qala.networking.NetDevice;
import io.qala.networking.TrafficStats;
import io.qala.networking.l2.L2Packet;
import io.qala.networking.l2.Mac;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpPacketType implements PacketType {
    private static final Logger LOGGER = LoggerFactory.getLogger(IpPacketType.class);
    private final FibTableList rtables;
    private final ArpTable arpTable;
    private final TrafficStats trafficStats = new TrafficStats();

    public IpPacketType(ArpTable arpTable, FibTableList rtables) {
        this.rtables = rtables;
        this.arpTable = arpTable;
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
            deliverLocally(l2.getDev(), ip);
            return;
        }
        rt = rtables.main().lookup(ip.dst());
        //need to forward the packet
    }
    public void send(IpAddress dst, Bytes body) {
        LOGGER.info("Sending IP packet to {}", dst);
        Route route = rtables.local().lookup(dst);
        if(route != null) {
            NetDevice dev = route.getDev();
            deliverLocally(dev, new IpPacket(dev.getMac(), dev.getIpAddress(), dev.getMac(), dst, body));
            return;
        }
        route = rtables.main().lookup(dst);
        if (route == null)
            throw RoutingException.networkUnreachable();
        NetDevice dev = route.getDev();
        Mac neighbor = arpTable.getNeighbor(dst, dev);
        IpPacket ipPacket = new IpPacket(dev.getMac(), dev.getIpAddress(), neighbor, dst, body);
        dev.send(ipPacket.toL2());
    }

    @Override
    public boolean matches(L2Packet l2) {
        return IpPacket.isIp(l2);
    }
    private void deliverLocally(NetDevice dev, IpPacket ipPacket) {
        trafficStats.receivedPacket(dev, ipPacket);
    }

    public TrafficStats getTrafficStats() {
        return trafficStats;
    }
}
