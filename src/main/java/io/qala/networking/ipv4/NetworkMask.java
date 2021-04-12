package io.qala.networking.ipv4;

import io.qala.networking.Bytes;

import java.util.Random;

public class NetworkMask {
    private final IpAddress networkPrefix;
    private final IpAddress netMask;

    /**
     * @param s 192.168.5.0/26
     */
    public NetworkMask(String s) {
        this(new IpAddress(s.split("/")[0]), new IpAddress(Bytes.highBits(4, Integer.parseInt(s.split("/")[1]))));
    }

    /**
     * https://en.wikipedia.org/wiki/Subnetwork#Determining_the_network_prefix
     *
     * @param networkPrefix 11000000.00000000.00000010.10000000 (192.0.2.128)
     * @param netMask 11111111.11111111.11111111.11000000	(255.255.255.192)
     */
    public NetworkMask(IpAddress networkPrefix, IpAddress netMask) {
        this.networkPrefix = networkPrefix;
        this.netMask = netMask;
    }
    public boolean matches(IpAddress address) {
        return netMask.toBytes().and(address.toBytes()).equals(networkPrefix.toBytes());
    }

    public IpAddress randomAddress() {
        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++) {
            result[i] = (byte) (netMask.get(i) & networkPrefix.get(i));
            if(result[i] == 0)
                result[i] = (byte) new Random().nextInt();
        }
        return new IpAddress(result);
    }

}
