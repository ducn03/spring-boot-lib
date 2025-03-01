package com.springboot.lib.queue.eventbus;

import lombok.CustomLog;
import org.springframework.stereotype.Service;

@Service
@CustomLog
public class EventBusProducer {
    private final EventBus eventBus;

    public EventBusProducer(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void sendEvent(String eventType, String message) {
        eventBus.publish(eventType, message);
        log.info("🚀 Gửi event tới " + eventType + ": " + message);
    }

}
