package com.springboot.prj.event;

import java.util.function.Consumer;

public interface EventHandler extends Consumer<String> {
    void handleEvent(String message);
}
