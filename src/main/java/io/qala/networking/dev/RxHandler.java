package io.qala.networking.dev;

import io.qala.networking.l2.L2Packet;

/**
 * rx_handlers are functions called from inside __netif_receive_skb(), to do special processing of the skb,
 * prior to delivery to protocol handlers.
 *
 * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/include/linux/netdevice.h#L397">docs</a>
 */
public interface RxHandler {
    /**
     * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/include/linux/netdevice.h#L436">rx_handler_func_t</a>
     */
    RxHandlerResult handle(L2Packet l2);

    class NoOpRxHandler implements RxHandler {
        @Override public RxHandlerResult handle(L2Packet l2) {
            return RxHandlerResult.RX_HANDLER_PASS;
        }
    }
}
