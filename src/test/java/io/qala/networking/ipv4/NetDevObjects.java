package io.qala.networking.ipv4;

import io.qala.networking.dev.NetDevice;
import io.qala.networking.dev.PhysicalDeviceSender;
import io.qala.networking.l1.EthNicDriver;
import io.qala.networking.l1.NicMicrocontroller;

public class NetDevObjects {
    public IpRange network;
    public IpAddress ipAddress;
    public NicMicrocontroller eth;
    public final NetDevice dev;

    NetDevObjects(NetDevice dev, PacketType[] packetTypes) {// for bridge, since it doesn't have all the fields
        this.dev = dev;
    }
    NetDevObjects(String devname, FibTableList rtables, PacketType[] packetTypes) {
        network = IpRange.random();
        ipAddress = network.randomAddr();
        eth = new NicMicrocontroller();
        EthNicDriver driver = new EthNicDriver(eth);
        eth.setDriver(driver);
        dev = new NetDevice(devname, driver, new PhysicalDeviceSender(driver), rtables, packetTypes);
        driver.setDevice(dev);
        dev.addIpAddress(new IpRange(ipAddress, network.getNetworkBitCount()));
    }
}
