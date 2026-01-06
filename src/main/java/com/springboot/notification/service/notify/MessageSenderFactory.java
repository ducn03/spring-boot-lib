package com.springboot.notification.service.notify;

import com.springboot.notification.service.notify.channels.AppSender;
import com.springboot.notification.service.notify.channels.EmailSender;
import com.springboot.notification.service.notify.channels.SmsSender;
import com.springboot.notification.service.notify.channels.ZaloSender;
import com.springboot.notification.service.notify.data.ENotifyMethod;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageSenderFactory {
    private final Map<ENotifyMethod, MessageSender> senderMap;

    public MessageSenderFactory(EmailSender emailSender,
                                AppSender appSender,
                                SmsSender smsSender,
                                ZaloSender zaloSender) {
        this.senderMap = Map.of(
                ENotifyMethod.EMAIL, emailSender,
                ENotifyMethod.APP, appSender,
                ENotifyMethod.SMS, smsSender,
                ENotifyMethod.ZALO, zaloSender
        );
    }

    public MessageSender getSender(ENotifyMethod method) {
        return senderMap.getOrDefault(method, null);
    }
}
