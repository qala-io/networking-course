package io.qala.networking.l2;

import io.qala.networking.Link;
import io.qala.networking.Nic;
import io.qala.networking.Port;
import io.qala.networking.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static io.qala.datagen.RandomShortApi.alphanumeric;

/**
 * Either a software bridge or a hardware switch - they do the same thing.
 * Watch <a href="https://www.youtube.com/watch?v=rYodcvhh7b8">this video</a>.
 */
public class Bridge implements Link<L2Packet>, Router<L2Packet> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bridge.class);
    /**
     * To list NICs who use the bridge: {@code ip link show master <bridge name>}
     */
    private final Map<Port, Nic> nics = new HashMap<>();
    private final Map<Link<L2Packet>, Port> ports = new HashMap<>();
    private final Map<Mac, Port> routing = new HashMap<>();
    private final String name = "br-" + alphanumeric(5);

    public Bridge(Nic ... nics) {
        LOGGER.info("ip link add name {} type bridge", name);
        LOGGER.info("ip link set dev {} up", name);
        for (Nic nic : nics) {
            Port port = new Port(this.nics.size());
            this.nics.put(port, nic);
            ports.put(nic, port);
            nic.setEndpoint(this);
        }
    }
    /**
     * @param src unfortunately in code our NIC doesn't know which port it's attached
     *            to and thus we have to pass the src NIC explicitly (for now)
     */
    public void route(Link<L2Packet> src, L2Packet packet) {
        Port srcPort = ports.get(src);
        routing.put(packet.src(), srcPort);
        Port dstPort = routing.get(packet.dst());
        if(dstPort != null) {
            send(dstPort, packet);
            return;
        }
        // if no such entry in existing ARP table, start "flooding"
        // broadcast won't be in the table either, so no need to do an explicit check - it'll get here anyway
        for (Port next : ports.values())
            if (!next.equals(srcPort))
                send(next, packet);

    }
    public void receive(Link<L2Packet> src, L2Packet packet) {
        route(src, packet);
    }

    public void attachWire(Port port, Nic endpoint) {
        nics.put(port, endpoint);
        ports.put(endpoint, port);
    }
    private void send(Port port, L2Packet packet) {
        nics.get(port).receive(this, packet);
        LOGGER.trace("Sending L2 package for {} to port {}", packet.dst(), port);
    }
    @Override public String toString() {
        return name;
    }
}
