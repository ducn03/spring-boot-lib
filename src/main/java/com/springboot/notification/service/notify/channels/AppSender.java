package com.springboot.notification.service.notify.channels;

import com.springboot.notification.service.notify.MessageSender;
import com.springboot.notification.service.notify.dto.NotifyRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AppSender implements MessageSender {
    @Override
    public boolean send(NotifyRequest notifyRequest) {
        return false;
    }

    @Override
    public long getBalance() {
        return 0;
    }
}
