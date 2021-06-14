package io.qala.networking.ipv4;

import io.qala.networking.EthNic;
import io.qala.networking.NetDevice;
import io.qala.networking.NetDeviceLogic;

public class NetDevObjects {
    public final IpRange network;
    public final IpAddress ipAddress;
    public final EthNic eth;
    public final NetDevice dev;

    NetDevObjects(FibTableList rtables, NetDeviceLogic netDeviceLogic) {
        network = IpRange.random();
        ipAddress = network.randomAddr();
        eth = new EthNic(netDeviceLogic);
        dev = new NetDevice(eth, rtables);
        dev.addIpAddress(new IpRange(ipAddress, network.getNetworkBitCount()));
    }
}
