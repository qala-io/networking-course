package io.qala.networking;

public interface Endpoint<T> {
    void process(Endpoint<T> src, T req);
}
