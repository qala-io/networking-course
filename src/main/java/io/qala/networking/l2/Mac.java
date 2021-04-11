package io.qala.networking.l2;

import java.util.Arrays;
import java.util.Objects;

public class Mac {
    private static final byte FF = (byte) 0xff;
    public static final Mac BROADCAST = new Mac(FF, FF, FF, FF, FF, FF);
    private final byte[] value;

    Mac(byte... value) {
        this.value = value;
    }

    public boolean isBroadcast() {
        for (int i = 0; i < value.length; i++)
            if (BROADCAST.value[i] != value[i])
                return false;
        return true;
    }
    @Override public String toString() {
        return "Mac{" +
                "value=" + Arrays.toString(value) +
                '}';
    }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mac mac = (Mac) o;
        return Objects.equals(value, mac.value);
    }
    @Override public int hashCode() {
        return Objects.hash(value);
    }
}
