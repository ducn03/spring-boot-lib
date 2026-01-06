package com.springboot.notification.service.notify.dto;

import com.springboot.notification.service.notify.data.ENotifyMethod;
import com.springboot.notification.service.notify.data.ENotifyType;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class NotifyRequest {
    private ENotifyMethod method;
    private ENotifyType type;
    private long userId;
    private String phone;
    private String email;

    /**
     * Nếu templateId = 0 thì sẽ gửi content đi nguyên vẹn<br>
     * Nếu templateId =/ 0 thì sẽ chèn content vào template rồi mới gửi
     */
    private int templateId;
    private String lang;
    private boolean isSaveNotification = false;

    /**
     * Not require
     */
    private String title;

    /**
     * Require
     */
    private String content;

    private Timestamp sendTime = new Timestamp(System.currentTimeMillis());
}
