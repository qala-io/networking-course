package io.qala.networking.l2;

import io.qala.networking.*;
import io.qala.networking.dev.BridgeSender;
import io.qala.networking.dev.NetDevice;
import io.qala.networking.dev.NetDeviceLogic;
import io.qala.networking.dev.RxHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Either a software bridge or a hardware switch - they do the same thing.
 * Watch <a href="https://www.youtube.com/watch?v=rYodcvhh7b8">this video</a>.
 *
 * Also see <a href="https://www.programmersought.com/article/5995656527/">an awesome article that describe Kernel code</a>
 */
public class Bridge {
    private static int count;
    /** Bridge's own device. */
    private final NetDevice brdev;
    private final Map<Mac, NetDevice> macToDev = new HashMap<>();
    /**
     * We don't introduce a class {@code Port} to keep things simple, it's easier to reference device directly.
     */
    private final Map<NetDevice, Mac> ports = new HashMap<>();

    public Bridge(NetDevice brdev) {
        this.brdev = brdev;
        this.brdev.rxHandlerRegister(new RxHandler.NoOpRxHandler());
    }
    public void addInterface(NetDevice dev) {
        rxHandlerRegister(dev);//https://elixir.bootlin.com/linux/v5.12.1/source/net/bridge/br_if.c#L636
        macToDev.put(dev.getHardwareAddress(), dev);
        if(ports.containsKey(dev))
            throw new RuntimeException("" + (-ErrNumbers.EBUSY.code));
        ports.put(dev, dev.getHardwareAddress());
    }
    public NetDevice getInterface(Mac mac) {
        return macToDev.get(mac);
    }
    /**
     * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/net/bridge/br_input.c#L33">br_pass_frame_up</a>
     * <a href="https://www.programmersought.com/article/9055656725/">More comments</a>
     */
    public void handleFrameFinish(L2Packet l2) {
        macToDev.put(l2.src(), l2.getDev());
        ports.put(l2.getDev(), l2.src());
        NetDevice dev = macToDev.get(l2.dst());
        if(dev != null)
            passFrameUp(l2);
        else
            // if no such entry in existing ARP table, start "flooding"
            // broadcast won't be in the table either, so no need to do an explicit check - it'll get here anyway
            flood(l2);
    }
    /**
     * <a href="https://www.programmersought.com/article/5995656527/">More comments</a>
     */
    private void passFrameUp(L2Packet l2) {
        // If we didn't change the device, it would invoke netdev logic again, which would again check
        // for rx_hanlder and that would call this bridge again, so we'd go in circles. By changing
        // the device though the rx_handler will be empty and thus the packet would be processed directly
        // in NetDevice w/o calling the bridge.
        l2.setDev(brdev);//https://elixir.bootlin.com/linux/v5.12.1/source/net/bridge/br_input.c#L53
        //NF_HOOK(NFPROTO_BRIDGE, NF_BR_LOCAL_IN,
        //		       dev_net(indev), NULL, skb, indev, NULL,
        //		       br_netif_receive_skb)
        brdev.receive(l2);
    }
    //https://elixir.bootlin.com/linux/v5.12.1/source/net/bridge/br_input.c#L168
    private void flood(L2Packet l2) {
        for (NetDevice nextDev : ports.keySet())
            if (nextDev != l2.getDev()) {
                L2Packet copy = new L2Packet(l2);//otherwise once the device is replaced with bridge - the if-condition above won't work
                passFrameUp(copy);// it doesn't actually call pass frame up when flooding, but for our purposes it's close enough
            }
    }

    /**
     * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/net/core/dev.c#L5097">netdev_rx_handler_register()</a>
     */
    private void rxHandlerRegister(NetDevice dev) {
        Loggers.TERMINAL_COMMANDS.info("ip link set dev {} master {}", dev, this.brdev);
        dev.rxHandlerRegister(getHandler());
    }
    /**
     * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/net/bridge/br_input.c#L387">br_get_rx_handler()</a>
     */
    private RxHandler getHandler() {
        return new CallBridgeRxHandler(this);
    }

    @Override public String toString() {
        return this.brdev.toString();
    }
}
