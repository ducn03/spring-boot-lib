package com.springboot.prj.event.eventbus;

import com.springboot.lib.helper.JsonHelper;
import com.springboot.prj.event.EventFactory;
import com.springboot.prj.event.EventHandler;
import com.springboot.prj.event.EventMessage;
import lombok.CustomLog;
import org.springframework.stereotype.Service;

@Service
@CustomLog
public class EventBusConsumer {
    private final EventFactory eventFactory;

    public EventBusConsumer(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }

    public void processMessage(String message) {
        EventMessage eventMessage = JsonHelper.toObject(message, EventMessage.class);
        if (eventMessage == null) {
            log.info("🚨 Gửi message bị lỗi, vui lòng coi lại message gửi đi: " + message);
            return;
        }

        EventHandler consumer = eventFactory.getConsumer(eventMessage.getType());
        if (consumer == null) {
            log.info("⚠ No consumer found for event: " + eventMessage.getType());
            return;
        }

        log.info("📩 Dispatching event: " + eventMessage.getType());
        consumer.handleEvent(eventMessage.getMessage());
    }
}
