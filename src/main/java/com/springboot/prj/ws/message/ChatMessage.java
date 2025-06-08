package com.springboot.prj.ws.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage extends Message {
    private String senderId;
    private String receiverId; // or groupId for group chat
    private String content;
    private long timestamp;
}
