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
    private boolean ipForward;

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
        LOGGER.info(l2.getDev() + " received IP from " + ip.src());
        Route rt = rtables.lookup(ip.dst());
        if(rt == null)
            return;
        if(rt.isLocal())
            ipLocalDeliver(l2.getDev(), ip);
        else if(ipForward)
            ipForward(rt, ip);
    }
    public void send(IpAddress dst, Bytes body) {
        LOGGER.info("Sending IP packet to {}", dst);
        Route rt = rtables.lookup(dst);
        if(rt == null)
            throw RoutingException.networkUnreachable();
        if(rt.isLocal()) {
            NetDevice dev = rt.getDev();
            ipLocalDeliver(dev, new IpPacket(dev.getMac(), dev.getIpAddress(), dev.getMac(), dst, body));
        } else {
            ipSendRemotely(dst, rt, body);
        }
    }

    @Override
    public boolean matches(L2Packet l2) {
        return IpPacket.isIp(l2);
    }

    /**
     * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/net/ipv4/ip_input.c#L240">ip_local_deliver()</a>
     */
    private void ipLocalDeliver(NetDevice dev, IpPacket ipPacket) {
        // invoke net filter
        ipLocalDeliverFinish(dev, ipPacket);
    }
    /**
     * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/net/ipv4/ip_input.c#L226">ip_local_deliver_finish()</a>
     */
    private void ipLocalDeliverFinish(NetDevice dev, IpPacket ipPacket) {
        trafficStats.receivedPacket(dev, ipPacket);
        // subsequent call stack:
        // ip_protocol_deliver_rcu(): https://elixir.bootlin.com/linux/v5.12.1/source/net/ipv4/ip_input.c#L187
        // ipprot->handler() which is dccp_v4_rcv(): https://elixir.bootlin.com/linux/v5.12.1/source/net/dccp/ipv4.c#L774
    }
    private void ipForward(Route rt, IpPacket ip) {
        ipForwardFinish(rt, ip);
    }
    private void ipForwardFinish(Route rt, IpPacket ip) {
        trafficStats.forwardPacket(rt.getDev(), ip);
        ipSendRemotely(ip.dst(), rt, ip.getPayload());
    }

    private void ipSendRemotely(IpAddress dst, Route route, Bytes body) {
        NetDevice dev = route.getDev();
        IpAddress nextHop = route.getNextHopFor(dst);
        Mac neighbor = arpTable.getNeighbor(nextHop, dev);
        IpPacket ipPacket = new IpPacket(dev.getMac(), dev.getIpAddress(), neighbor, dst, body);
        dev.send(ipPacket.toL2());
    }
    public TrafficStats getTrafficStats() {
        return trafficStats;
    }
    public void enableIpForward() {
        this.ipForward = true;
    }
}
