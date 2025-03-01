package com.springboot.prj.event;

import com.springboot.lib.queue.eventbus.EventBus;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EventFactory {
    private final EventBus eventBus;
    private final Map<String, EventHandler> consumers;

    public EventFactory(Map<String, EventHandler> consumers, EventBus eventBus) {
        this.eventBus = eventBus;
        this.consumers = Map.of(
                // EventTypes.CHART.ADD, chartConfigConsumer
        );
    }

    @PostConstruct
    public void registerConsumers() {
        consumers.forEach(eventBus::register);
    }

    public EventHandler getConsumer(String eventType) {
        return consumers.get(eventType);
    }

}
