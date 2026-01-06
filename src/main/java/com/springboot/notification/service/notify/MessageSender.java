package com.springboot.notification.service.notify;

import com.springboot.notification.service.notify.dto.NotifyRequest;

import java.io.IOException;

public interface MessageSender {
    /**
     * Gửi thông báo
     * @param notifyRequest
     * @return true/false
     */
    boolean send(NotifyRequest notifyRequest);

    /**
     * Đối với những phương thức gửi message tính phí sẽ có số dư
     * @return số dư hiện tại
     */
    long getBalance();
}
