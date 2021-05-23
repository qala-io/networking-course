package io.qala.networking.ipv4;

import java.util.Objects;
import java.util.Random;

import static io.qala.datagen.RandomShortApi.integer;

/**
 * <a href="https://en.wikipedia.org/wiki/Classless_Inter-Domain_Routing">CIDR</a> contains an IP address followed
 * by the number of bits given for the network, e.g. 192.168.0.1/26.
 */
public class IpRange {
    private final IpAddress address;
    private final IpAddress mask;

    /**
     * @param cidr a <a href="https://en.wikipedia.org/wiki/Classless_Inter-Domain_Routing">CIDR</a>, meaning a
     *             range of addresses in a form of {@code 192.168.5.0/26}
     */
    public IpRange(String cidr) {
        this(new IpAddress(cidr.split("/")[0]), Integer.parseInt(cidr.split("/")[1]));
    }
    public IpRange(IpAddress address, int networkBits) {
        this.address = address;
        if(networkBits == 0) {
            this.mask = IpAddress.MIN;
        } else {
            int zeroBitCount = 32 - networkBits;
            this.mask = new IpAddress(-(1 << zeroBitCount));
        }
    }
    /**
     * https://en.wikipedia.org/wiki/Subnetwork#Determining_the_network_prefix
     *
     * @param address 11000000.00000000.00000010.10000000 (192.0.2.128)
     * @param mask 11111111.11111111.11111111.10000000	(255.255.255.192)
     */
    public IpRange(IpAddress address, IpAddress mask) {
        this.address = address;
        this.mask = mask;
    }
    public boolean matches(IpAddress address) {
        return (mask.asInt() & address.asInt()) == this.address.asInt();
    }
    public IpAddress getAddress() {
        return address;
    }

    public static IpRange randomAddressInRange() {
        IpRange random = random();
        return new IpRange(random.randomAddr(), random.mask);
    }
    public static IpRange random() {
        int maskSize = integer(7, 25);
        int mask = -(1 << maskSize);
        int ip = integer();
        return new IpRange(new IpAddress(ip & mask), new IpAddress(mask));
    }
    public IpAddress randomAddr() {
        int i = new Random().nextInt();
        return new IpAddress(address.asInt() | (i & ~mask.asInt()));
    }
    public int getNetworkBitCount() {
        int mask = this.mask.asInt();
        int ones = 0;
        while(mask != 0) {
            mask <<= 1;
            ones++;
        }
        return ones;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IpRange ipRange = (IpRange) o;
        return address.equals(ipRange.address) && mask.equals(ipRange.mask);
    }
    @Override public int hashCode() {
        return Objects.hash(address, mask);
    }
    @Override public String toString() {
        return address + "/" + getNetworkBitCount();
    }
}
