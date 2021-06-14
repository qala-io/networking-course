package io.qala.networking;

import io.qala.networking.ipv4.*;
import io.qala.networking.l2.L2Packet;
import io.qala.networking.l2.Mac;

import java.util.HashSet;
import java.util.Set;

public class NetDevice {
    private static int counter = 0;
    private final String name;
    private final FibTableList fib;
    private final Nic2 nic;

    /**
     * This is actually part of internet device, not just device.
     * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/include/linux/inetdevice.h#L29">in_device->in_ifaddr</a>
     */
    private final Set<IpRange> ipAddresses = new HashSet<>();
    private RxHandler rxHandler = new RxHandler.NoOpRxHandler();

    public NetDevice(String name) {
        this.fib = null;
        this.nic = null;
        this.name = name;
    }
    public NetDevice(Nic2 nic, FibTableList fib) {
        this.name = "eth" + counter++;
        this.nic = nic;
        this.fib = fib;
        nic.connectLinuxDevice(this);
        Loggers.TERMINAL_COMMANDS.info("ip link add {} type eth??", name);
    }

    public void send(L2Packet l2) {
        nic.send(l2);
    }

    public Mac getMac() {
        return nic.getMac();
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

    public RxHandler getRxHandler() {
        return rxHandler;
    }
    @Override
    public String toString() {
        return name;
    }
}
