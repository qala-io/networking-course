package io.qala.networking;

public interface Link<T> {
    void receive(Link<T> src, T req);
}
