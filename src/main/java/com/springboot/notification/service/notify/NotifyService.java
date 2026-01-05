package com.springboot.notification.service.notify;

import com.springboot.notification.service.notify.dto.NotifyRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotifyService {
    private final MessageSenderFactory senderFactory;

    public NotifyService(MessageSenderFactory senderFactory) {
        this.senderFactory = senderFactory;
    }

    public boolean sendMessage(NotifyRequest notifyRequest) {
        MessageSender sender = senderFactory.getSender(notifyRequest.getMethod());
        return sender.send(notifyRequest);
    }
}
