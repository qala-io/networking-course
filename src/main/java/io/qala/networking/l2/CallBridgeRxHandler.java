package io.qala.networking.l2;

import io.qala.networking.RxHandler;
import io.qala.networking.RxHandlerResult;

class CallBridgeRxHandler implements RxHandler {
    private final Bridge bridge;

    public CallBridgeRxHandler(Bridge bridge) {
        this.bridge = bridge;
    }
    /**
     * <a href="https://elixir.bootlin.com/linux/v5.12.1/source/net/bridge/br_input.c#L272">br_handle_frame</a>
     */
    @Override public RxHandlerResult handle(L2Packet l2) {
        bridge.handleFrameFinish(l2);
        return RxHandlerResult.RX_HANDLER_CONSUMED;
    }
}
