package com.springboot.prj.event.handler;

import com.springboot.lib.helper.JsonHelper;
import com.springboot.prj.event.EventHandler;
import com.springboot.prj.event.message.TestMessage;
import lombok.CustomLog;
import org.springframework.stereotype.Component;

@Component
@CustomLog
public class TestHandler implements EventHandler {
    @Override
    public void handleEvent(String message) {
        TestMessage testMessage = JsonHelper.toObject(message, TestMessage.class);
        if (testMessage == null) {
            log.info("Fail parse message");
            return;
        }
        log.info(testMessage.getType());
        log.info(String.valueOf(testMessage.getNumber()));
        log.info(String.valueOf(testMessage.isTest()));
    }

    @Override
    public void accept(String s) {

    }
}
