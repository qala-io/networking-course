package io.qala.networking.l2;

import io.qala.networking.Port;
import io.qala.networking.Nic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Either a software bridge or a hardware switch - they do the same thing.
 */
class Bridge {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bridge.class);
    /**
     * To list NICs who use the bridge: {@code ip link show master <bridge name>}
     */
    private final Map<Port, Nic> ports = new HashMap<>();
    private final Map<Mac, Port> routing = new HashMap<>();

    void receive(Port srcPort, L2Packet packet) {
        routing.put(packet.src(), srcPort);
        Port dstPort = routing.get(packet.dst());
        if(dstPort != null)
            send(dstPort, packet);
        else // broadcast won't be in the table either, so no need to do an explicit check - it'll get here anyway
            for (Port next : ports.keySet())
                if(!next.equals(srcPort))
                    send(next, packet);
    }
    void send(Port port, L2Packet packet) {
        ports.get(port).process(packet);
        LOGGER.trace("Sending L2 package to: {}", packet.dst());
    }
    public void attachWire(Port port, Nic endpoint) {
        ports.put(port, endpoint);
    }
}
