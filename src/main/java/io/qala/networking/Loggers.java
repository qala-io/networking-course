package io.qala.networking;

import io.qala.networking.l2.Bridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Loggers {
    Logger TERMINAL_COMMANDS = LoggerFactory.getLogger("$");
}
