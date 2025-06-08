package com.springboot.prj.ws.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CartMessage.class, name = MessageType.CART),
        @JsonSubTypes.Type(value = ChatMessage.class, name = MessageType.CHAT),
        @JsonSubTypes.Type(value = NotifyMessage.class, name = MessageType.NOTIFY)
})
@Getter
@Setter
public abstract class Message {
    /**
     * Loại tin nhắn: cart, chat, notify
     */
    private String type;
}
