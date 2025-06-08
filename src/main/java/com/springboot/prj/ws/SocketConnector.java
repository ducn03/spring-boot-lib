package com.springboot.prj.ws;

import com.springboot.lib.helper.JsonHelper;
import com.springboot.prj.ws.message.Message;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.CustomLog;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * PORT SOCKET ĐI THEO PORT SERVER
 */
@ServerEndpoint(value = "/webSocket", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
@Component
@CustomLog
public class SocketConnector {
    private SocketListener listener;

    @OnOpen
    public void onOpen(Session session) {
        this.listener = SocketManager.getInstance().getListener();
        this.listener.onOpen(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        try {
            Message msg = JsonHelper.toObject(message, Message.class);
            if (this.listener != null) {
                this.listener.onMessage(session, msg);
            }
        } catch (Exception e) {
            log.info(String.format("Failed to parse message: %s", message));
        }
    }

    @OnClose
    public void onClose(CloseReason reason, Session session) {
        if (this.listener != null) {
            this.listener.onClose(session, reason);
        }
    }

    @OnError
    public void onError(Session session, Throwable t) {
        if (this.listener != null) {
            this.listener.onError(session, t);
            this.listener = null;
        }
    }
}
