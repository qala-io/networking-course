package io.qala.networking;

/**
 * https://elixir.bootlin.com/linux/v5.12.1/source/include/uapi/asm-generic/errno-base.h#L20
 */
public enum ErrNumbers {
    /** Device or resource busy */
    EBUSY(16);
    public final int code;

    ErrNumbers(int code) {
        this.code = code;
    }
}
