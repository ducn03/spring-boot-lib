package com.springboot.lib.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SpringbootLogger implements ILogger {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    private static SpringbootLogger logger;
    public static SpringbootLogger getInstance() {
        if (logger != null) {
            return logger;
        }
        return logger = new SpringbootLogger();
    }

    @Override
    public void info(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        LOG.info(message);
    }

    @Override
    public void error(String message, Throwable e) {
        LOG.error(message, e);
    }
}
