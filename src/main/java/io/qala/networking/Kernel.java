package io.qala.networking;

import io.qala.networking.ipv4.ArpPacket;
import io.qala.networking.ipv4.IpPacket;
import io.qala.networking.ipv4.Router;

import java.util.ArrayList;
import java.util.List;

public class Kernel {
    private final List<Nic> nics = new ArrayList<>();
    private final Router routes = new Router();

    public void process(ArpPacket packet) {

    }
    public void process(IpPacket ipPacket) {

    }
    public void route(IpPacket ipPacket) {

    }
}
