package io.qala.networking;

import io.qala.networking.ipv4.PacketType;
import io.qala.networking.l2.L2Packet;

public class NetDeviceLogic {
    private final PacketType[] packetTypes;

    public NetDeviceLogic(PacketType[] packetTypes) {
        this.packetTypes = packetTypes;
    }
    /**
     * <a href=https://elixir.bootlin.com/linux/v5.12.1/source/net/core/dev.c#L5656">netif_receive_skb()</a> ->
     * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/net/core/dev.c#L5478">__netif_receive_skb()</a> ->
     * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/net/core/dev.c#L5376"> __netif_receive_skb_one_core()</a> ->
     * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/net/core/dev.c#L5174">__netif_receive_skb_core()</a>
     *
     * See <a href="https://www.programmersought.com/article/48945953151/">More comments on Kernel source code</a>.
     */
    public void receive(L2Packet l2) {
        RxHandlerResult ret = l2.getDev().getRxHandler().handle(l2);//https://elixir.bootlin.com/linux/v5.12.1/source/net/core/dev.c#L5274
        if(ret == RxHandlerResult.RX_HANDLER_CONSUMED)
            return; //https://elixir.bootlin.com/linux/v5.12.1/source/net/core/dev.c#L5281
        LogPadding.pad();
        for (PacketType packetType : packetTypes)
            if (packetType.matches(l2)) {
                packetType.rcv(l2);
                break;
            }
        LogPadding.unpad();
    }
}
