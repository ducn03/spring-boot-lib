package com.springboot.notification.service.notify;

import com.springboot.notification.service.notify.channels.AppSender;
import com.springboot.notification.service.notify.channels.EmailSender;
import com.springboot.notification.service.notify.data.ENotifyMethod;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageSenderFactory {
    private final Map<ENotifyMethod, MessageSender> senderMap;

    public MessageSenderFactory(EmailSender emailSender, AppSender appSender) {
        this.senderMap = Map.of(
                ENotifyMethod.EMAIL, emailSender,
                ENotifyMethod.APP, appSender
        );
    }

    public MessageSender getSender(ENotifyMethod method) {
        return senderMap.getOrDefault(method, null);
    }
}
