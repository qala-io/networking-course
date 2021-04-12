package io.qala.networking.ipv4;

import io.qala.networking.Nic;

import java.util.HashMap;
import java.util.Map;

public class Router {
    private final Map<NetworkMask, Nic> table = new HashMap<>();
}
