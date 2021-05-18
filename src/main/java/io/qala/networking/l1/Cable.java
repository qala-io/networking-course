package io.qala.networking.l1;

import io.qala.networking.Bytes;
import io.qala.networking.Nic2;

public class Cable {
    private final Nic2 nic1, nic2;

    public Cable(Nic2 nic1, Nic2 nic2) {
        this.nic1 = nic1;
        this.nic2 = nic2;
        nic1.attachToCable(this);
        nic2.attachToCable(this);
    }

    public void send(Nic2 from, Bytes bytes) {
        Nic2 receiver;
        if(nic1 == from)
            receiver = nic2;
        else if (nic2 == from)
            receiver = nic1;
        else
            throw new IllegalArgumentException(from + " isn't connected to this cable");
        receiver.receive(bytes);
    }

}
