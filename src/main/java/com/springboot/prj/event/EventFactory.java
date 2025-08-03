package com.springboot.prj.event;

import com.springboot.lib.queue.eventbus.EventBus;
import com.springboot.prj.event.handler.TestHandler;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EventFactory {
    private final EventBus eventBus;
    private final Map<String, EventHandler> consumers;

    public EventFactory(Map<String, EventHandler> consumers, EventBus eventBus, TestHandler testHandler) {
        this.eventBus = eventBus;
        this.consumers = Map.of(
                // EventTypes.CHART.ADD, chartConfigConsumer
                EventType.TEST.TEST, testHandler
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
