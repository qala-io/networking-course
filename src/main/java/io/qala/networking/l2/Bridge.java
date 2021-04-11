package io.qala.networking.l2;

import io.qala.networking.L2Packet;
import io.qala.networking.Port;

import java.util.HashMap;
import java.util.Map;

/**
 * Either a software bridge or a hardware switch - they do the same thing.
 */
class Bridge {
    private final Port[] allPorts = new Port[255];
    private final Map<Mac, Port> routing = new HashMap<>();

    void process(Port srcPort, L2Packet packet) {
        routing.put(packet.getSrc(), srcPort);
        Port dstPort = routing.get(packet.getDst());
        if(dstPort != null)
            send(dstPort, packet);
        else
            for (Port next : allPorts)
                if(!next.equals(srcPort))
                    send(next, packet);
    }
    void send(Port port, L2Packet packet) {
        // actually send the packet
    }
    //ip link show master docker0
}
