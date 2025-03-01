package com.springboot.lib.queue.eventbus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomEvent {
    private final String topic;
    private final String message;

    public CustomEvent(String topic, String message) {
        this.topic = topic;
        this.message = message;
    }
}
