package io.qala.networking.l2;

import java.util.Objects;

class Mac {
    private final String value;

    Mac(String value) {
        this.value = value;
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
