package com.springboot.lib.queue.eventbus;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
public class EventBus {
    private final Map<String, List<Consumer<String>>> listeners = new ConcurrentHashMap<>();

    public void register(String eventType, Consumer<String> consumer) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(consumer);
    }

    public void publish(String eventType, String message) {
        List<Consumer<String>> consumers = listeners.get(eventType);
        if (consumers != null) {
            consumers.forEach(consumer -> consumer.accept(message));
        }
    }
}
