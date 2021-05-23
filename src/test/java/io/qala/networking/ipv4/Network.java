package io.qala.networking.ipv4;

import io.qala.networking.EthNic;
import io.qala.networking.NetDevice;

class Network {
    final IpRange network;
    final IpAddress ipAddress;
    final EthNic eth;
    final NetDevice dev;

    Network(RoutingTables rtables, PacketType[] packetTypes) {
        network = IpRange.randomAddressInRange();
        ipAddress = network.getAddress();
        eth = new EthNic();
        dev = new NetDevice(eth, packetTypes, rtables);
        dev.addIpAddress(network);
    }
}
