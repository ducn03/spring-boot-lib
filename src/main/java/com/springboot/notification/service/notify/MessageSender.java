package com.springboot.notification.service.notify;

import com.springboot.notification.service.notify.dto.NotifyRequest;

public interface MessageSender {
    /**
     * Gửi thông báo
     * @param notifyRequest
     * @return true/false
     */
    boolean send(NotifyRequest notifyRequest);
}
