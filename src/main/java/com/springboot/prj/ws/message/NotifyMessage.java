package com.springboot.prj.ws.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotifyMessage extends Message {
    private String notifyType; // e.g., "promotion", "order_update"
    private String title;
    private String content;
    private long timestamp;
}
