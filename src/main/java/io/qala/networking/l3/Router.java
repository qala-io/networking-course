package io.qala.networking.l3;

import io.qala.networking.Nic;

import java.util.HashMap;
import java.util.Map;

public class Router {
    private final Map<IpAddress/*mask*/, Nic> table = new HashMap<>();
}
