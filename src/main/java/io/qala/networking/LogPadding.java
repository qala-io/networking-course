package io.qala.networking;

import org.slf4j.MDC;

public class LogPadding {
    public static void pad() {
        String oldPadding = MDC.get("padding");
        oldPadding = oldPadding == null ? "" : oldPadding;
        MDC.put("padding", oldPadding + " ");
    }
    public static void unpad() {
        String padding = MDC.get("padding");
        MDC.put("padding", padding.substring(0, padding.length() - 1));
    }
}
