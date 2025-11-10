package com.springboot.lib.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AppLogger implements ILogger {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    private static AppLogger logger;
    public static AppLogger getInstance() {
        if (logger != null) {
            return logger;
        }
        return logger = new AppLogger();
    }

    @Override
    public void info(String message) {
        LOG.info(message);
    }

    @Override
    public void debug(String message) {
        LOG.info("(" + Thread.currentThread().getId() + "): " + message);
    }

    @Override
    public void error(String message, Throwable e) {
        LOG.error(message, e);
    }
}
