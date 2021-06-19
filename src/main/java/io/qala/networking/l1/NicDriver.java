package io.qala.networking.l1;

import io.qala.networking.dev.NetDevice;
import io.qala.networking.l2.L2Packet;
import io.qala.networking.l2.Mac;

/**
 * E.g. eth driver
 */
public class NicDriver {
    private final NicMicrocontroller nic;
    private NetDevice dev;

    public NicDriver(NicMicrocontroller nic) {
        this.nic = nic;
    }
    public void setDevice(NetDevice dev) {
        this.dev = dev;
    }
    public void send(L2Packet packet) {
        nic.send(packet.toBytes());
    }
    public void receive(L2Packet packet) {
        packet.setDev(dev);
        dev.receive(packet);
    }
    public Mac getMac() {
        return nic.getMac();
    }
    public void enterPromiscuousMode() {
        this.nic.enterPromiscMode();
    }
}
