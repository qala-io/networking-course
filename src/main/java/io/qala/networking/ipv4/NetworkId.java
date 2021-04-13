package io.qala.networking.ipv4;

import java.util.Random;

import static io.qala.datagen.RandomShortApi.integer;

public class NetworkId {
    private final IpAddress network;
    private final IpAddress netMask;

    /**
     * @param s 192.168.5.0/26
     */
    public NetworkId(String s) {
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
    public NetworkId(IpAddress network, IpAddress netMask) {
        this.network = network;
        this.netMask = netMask;
    }
    public boolean matches(IpAddress address) {
        return (netMask.asInt() & address.asInt()) == network.asInt();
    }

    public static NetworkId random() {
        int maskSize = integer(7, 25);
        int mask = -(1 << maskSize);
        int ip = integer();
        return new NetworkId(new IpAddress(ip & mask), new IpAddress(mask));
    }
    public IpAddress randomAddr() {
        int i = new Random().nextInt();
        return new IpAddress(network.asInt() | (i & ~netMask.asInt()));
    }

    @Override public String toString() {
        int mask = netMask.asInt();
        int ones = 0;
        while(mask != 0) {
            mask <<= 1;
            ones++;
        }
        return network + "/" + ones;
    }
}
