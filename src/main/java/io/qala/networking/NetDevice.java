package io.qala.networking;

import io.qala.networking.ipv4.*;
import io.qala.networking.l2.L2Packet;
import io.qala.networking.l2.Mac;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static io.qala.datagen.RandomShortApi.numeric;

public class NetDevice {
    private static final Logger LOGGER = LoggerFactory.getLogger(NetDevice.class);
    private final PacketType[] packetTypes;
    private final String name = "eth" + numeric(5);
    private final RoutingTables rtables;
    private NetDevice master;
    private Nic2 nic;
    /**
     * This is actually part of internet device, not just device.
     * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/include/linux/inetdevice.h#L29">in_device->in_ifaddr</a>
     */
    private final Set<IpRange> ipAddresses = new HashSet<>();

    public NetDevice(Nic2 nic, PacketType[] packetTypes, RoutingTables rtables) {
        this.packetTypes = packetTypes;
        this.nic = nic;
        this.rtables = rtables;
        nic.connectLinuxDevice(this);
        Loggers.TERMINAL_COMMANDS.info("ip link add {} type eth??", name);
    }

    /**
     * <a href=https://elixir.bootlin.com/linux/v5.12.1/source/net/core/dev.c#L5656">netif_receive_skb</a>
     */
    public void receive(L2Packet l2) {
        if(master != null) {
            master.receive(l2);
            return;
        }
        LOGGER.info(this + " received IP packet from " + l2.src());
        LogPadding.pad();
        for (PacketType packetType : packetTypes) {
            if(packetType.matches(l2)) {
                packetType.rcv(l2);
                break;
            }
        }
        LogPadding.unpad();
    }
    public void send(L2Packet l2) {
        nic.send(l2);
    }

    /**
     * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/net/ipv4/devinet.c#L1370">confirm_addr_indev()</a>
     */
    public boolean matches(IpAddress ipAddress) {
        return ipAddresses.contains(ipAddress);
    }
    public Mac getMac() {
        return nic.getMac();
    }
    public void addIpAddress(IpRange range) {
        // in reality we add more routes - not just directly for the IP address, but for the whole network
        rtables.local().add(new Route(new IpRange(range.getAddress(), 32), null, this, RouteType.LOCAL));
        ipAddresses.add(range);
        // for now we just log the network, but we also need to let routing table read it
        Loggers.TERMINAL_COMMANDS.info("ip addr add {} dev {}", range, name);
    }
    public void setMaster(NetDevice master) {
        Loggers.TERMINAL_COMMANDS.info("ip link set dev {} master {}", name, master);
        this.master = master;
    }
    public void setNic(Nic2 nic) {
        this.nic = nic;
    }

    @Override
    public String toString() {
        return name;
    }
}
