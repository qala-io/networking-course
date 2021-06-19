package io.qala.networking.dev;

/**
 * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/include/linux/netdevice.h#L429">rx_handler_result</a>
 */
public enum RxHandlerResult {
    /**
     * skb was consumed by rx_handler, do not process it further.
     */
    RX_HANDLER_CONSUMED,
    /**
     * Do another round in receive path. This is indicated in case skb->dev was changed by rx_handler.
     */
    RX_HANDLER_ANOTHER,
    /**
     * Force exact delivery, no wildcard.
     */
    RX_HANDLER_EXACT,
    /**
     * Do nothing, pass the skb as if no rx_handler was called.
     */
    RX_HANDLER_PASS
}
