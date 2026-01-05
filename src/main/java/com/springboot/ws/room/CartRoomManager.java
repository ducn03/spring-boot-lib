package com.springboot.ws.room;

import com.springboot.lib.helper.JsonHelper;
import com.springboot.lib.service.redis.Redis;
import com.springboot.ws.SessionManager;
import com.springboot.ws.message.Message;
import jakarta.websocket.Session;
import lombok.CustomLog;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@CustomLog
public class CartRoomManager implements RoomManager {
    private static final String CART_ROOM_PREFIX = "com.hc.bff.socket.room.cart.";
    private final Redis redis;
    private final SessionManager sessionManager;

    public CartRoomManager(Redis redis, SessionManager sessionManager) {
        this.redis = redis;
        this.sessionManager = sessionManager;
    }

    @Override
    public void joinRoom(String cartCode, String signature, Session session) {
        redis.hSet(CART_ROOM_PREFIX + cartCode, signature, "1");
        sessionManager.addSession(signature, session);
        log.info("Client " + signature + " tham gia phòng cart " + cartCode);
    }

    @Override
    public void leaveRoom(String cartCode, String signature) {
        redis.hashDelete(CART_ROOM_PREFIX + cartCode, signature);
        log.info("Client " + signature + " rời phòng cart " + cartCode);
    }

    @Override
    public void sendMessage(String cartCode, Message message) {
        Set<Object> clients = redis.hGetAll(CART_ROOM_PREFIX + cartCode);
        if (clients == null || clients.isEmpty()) {
            log.info("Không có client trong phòng cart " + cartCode);
            return;
        }
        String json = JsonHelper.toJson(message);
        for (Object client : clients) {
            String signature = client.toString();
            Set<Session> sessions = sessionManager.getSessions(signature); // Dùng getSessions để lấy tất cả session
            for (Session session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.getBasicRemote().sendText(json);
                    } catch (Exception e) {
                        log.info("Gửi tin nhắn thất bại đến session " + signature + ": " + e.getMessage());
                    }
                }
            }
        }
    }
}