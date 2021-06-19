package io.qala.networking.ipv4;

import io.qala.networking.dev.NetDevice;
import io.qala.networking.dev.PhysicalDeviceSender;
import io.qala.networking.l1.NicDriver;
import io.qala.networking.l1.NicMicrocontroller;

public class NetDevObjects {
    public final IpRange network;
    public final IpAddress ipAddress;
    public final NicMicrocontroller eth;
    public final NetDevice dev;

    NetDevObjects(FibTableList rtables, PacketType[] packetTypes) {
        network = IpRange.random();
        ipAddress = network.randomAddr();
        eth = new NicMicrocontroller();
        NicDriver driver = new NicDriver(eth);
        eth.setDriver(driver);
        dev = new NetDevice(driver, new PhysicalDeviceSender(driver), rtables, packetTypes);
        driver.setDevice(dev);
        dev.addIpAddress(new IpRange(ipAddress, network.getNetworkBitCount()));
    }
}
