package com.springboot.lib.logging;

public interface ILogger {

    public void info(String message);

    void error(String format, Throwable e);
}
