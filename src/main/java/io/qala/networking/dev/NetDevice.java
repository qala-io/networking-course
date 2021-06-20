package io.qala.networking.dev;

import io.qala.networking.Loggers;
import io.qala.networking.ipv4.*;
import io.qala.networking.l1.EthNicDriver;
import io.qala.networking.l2.L2Packet;
import io.qala.networking.l2.Mac;

import java.util.HashSet;
import java.util.Set;

public class NetDevice {
    private final String name;
    private final FibTableList fib;
    private final EthNicDriver nic;
    private final Mac hardwareAddress;
    private final NetDevSender sender;
    private final PacketType[] packetTypes;

    /**
     * This is actually part of internet device, not just device.
     * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/include/linux/inetdevice.h#L29">in_device->in_ifaddr</a>
     */
    private final Set<IpRange> ipAddresses = new HashSet<>();
    /**
     * Called before device logic for packet receiving is invoked so that it could steal the work from
     * the device. Used e.g. for bridging ({@link io.qala.networking.l2.Bridge}).
     */
    private RxHandler rxHandler = new RxHandler.NoOpRxHandler();

    public NetDevice(String name, NetDevSender sender, PacketType[] packetTypes) {
        this.sender = sender;
        this.fib = null;
        this.nic = null;
        this.packetTypes = packetTypes;
        // virtual devices like bridges also require MAC even though they don't have a physical device to connect to
        this.hardwareAddress = Mac.random();
        this.name = name;
    }
    public NetDevice(String name, EthNicDriver nic, NetDevSender sender, FibTableList fib, PacketType[] packetTypes) {
        this.sender = sender;
        this.packetTypes = packetTypes;
        this.name = name;
        this.hardwareAddress = nic.getMac();
        this.nic = nic;
        this.fib = fib;
        Loggers.TERMINAL_COMMANDS.info("ip link add {} type {}", name, nic);
    }

    public void send(L2Packet l2) {
        sender.send(l2);
    }
    public void receive(L2Packet l2) {
        new NetDeviceLogic(packetTypes).receive(l2);
    }

    public Mac getHardwareAddress() {
        return hardwareAddress;
    }
    public IpAddress getIpAddress() {// let's just return the 1st address available for simplicity
        return ipAddresses.iterator().next().getAddress();
    }
    public void addIpAddress(IpRange range) {
        // in reality we add more routes - not just directly for the IP address, but for the whole network
        fib.local().add(new Route(new IpRange(range.getAddress(), 32), null, this, RouteType.LOCAL));
        // in reality this happens only if the interface is UP
        fib.main().add(new Route(range.toSubnet(), null, this, RouteType.REMOTE));
        ipAddresses.add(range);
        // for now we just log the network, but we also need to let routing table read it
        Loggers.TERMINAL_COMMANDS.info("ip addr add {} dev {}", range, name);
    }
    public void rxHandlerRegister(RxHandler rxHandler) {
        this.rxHandler = rxHandler;
    }
    public void enterPromiscuousMode() {
        nic.enterPromiscuousMode();
    }
    public RxHandler getRxHandler() {
        return rxHandler;
    }
    @Override
    public String toString() {
        return name;
    }
}
