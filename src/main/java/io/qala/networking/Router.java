package io.qala.networking;

public interface Router<PACKET_TYPE> {
    void route(Link<PACKET_TYPE> src, PACKET_TYPE packet);
}
