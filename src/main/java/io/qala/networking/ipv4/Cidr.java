package io.qala.networking.ipv4;

import java.util.Random;

import static io.qala.datagen.RandomShortApi.integer;

/**
 * <a href="https://en.wikipedia.org/wiki/Classless_Inter-Domain_Routing">CIDR</a> contains an IP address followed
 * by the number of bits given for the network, e.g. 192.168.0.1/26.
 */
public class Cidr {
    private final IpAddress network;
    private final IpAddress netMask;

    /**
     * @param s 192.168.5.0/26
     */
    public Cidr(String s) {
        String[] split = s.split("/");
        this.network = new IpAddress(split[0]);
        int zeroBitCount = 32 - Integer.parseInt(split[1]);
        this.netMask = new IpAddress(-(1 << zeroBitCount));
    }

    /**
     * https://en.wikipedia.org/wiki/Subnetwork#Determining_the_network_prefix
     *
     * @param network 11000000.00000000.00000010.10000000 (192.0.2.128)
     * @param netMask 11111111.11111111.11111111.10000000	(255.255.255.192)
     */
    public Cidr(IpAddress network, IpAddress netMask) {
        this.network = network;
        this.netMask = netMask;
    }
    public boolean matches(IpAddress address) {
        return (netMask.asInt() & address.asInt()) == network.asInt();
    }

    public static Cidr random() {
        int maskSize = integer(7, 25);
        int mask = -(1 << maskSize);
        int ip = integer();
        return new Cidr(new IpAddress(ip & mask), new IpAddress(mask));
    }
    public IpAddress randomAddr() {
        int i = new Random().nextInt();
        return new IpAddress(network.asInt() | (i & ~netMask.asInt()));
    }
    public int getNetworkBitCount() {
        int mask = netMask.asInt();
        int ones = 0;
        while(mask != 0) {
            mask <<= 1;
            ones++;
        }
        return ones;
    }

    @Override public String toString() {
        return network + "/" + getNetworkBitCount();
    }
}
