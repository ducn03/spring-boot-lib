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
public class NotifyRoomManager implements RoomManager {
    private static final String NOTIFY_ROOM_PREFIX = "com.springboot.prj.ws.room.notify.";
    private final Redis redis;
    private final SessionManager sessionManager;

    public NotifyRoomManager(Redis redis, SessionManager sessionManager) {
        this.redis = redis;
        this.sessionManager = sessionManager;
    }

    @Override
    public void joinRoom(String notifyType, String signature, Session session) {
        redis.hSet(NOTIFY_ROOM_PREFIX + notifyType, signature, "1");
        sessionManager.addSession(signature, session);
        log.info("Client " + signature + " tham gia phòng notify " + notifyType);
    }

    @Override
    public void leaveRoom(String notifyType, String signature) {
        redis.hashDelete(NOTIFY_ROOM_PREFIX + notifyType, signature);
        // Không xóa hết session, để lại để kiểm tra nếu cần
        log.info("Client " + signature + " rời phòng notify " + notifyType);
    }

    @Override
    public void sendMessage(String notifyType, Message message) {
        Set<Object> clients = redis.hGetAll(NOTIFY_ROOM_PREFIX + notifyType); // Sửa dùng NOTIFY_ROOM_PREFIX
        if (clients == null || clients.isEmpty()) {
            log.info("Không có client trong phòng notify " + notifyType);
            return;
        }
        String json = JsonHelper.toJson(message);
        for (Object client : clients) {
            String signature = client.toString();
            Set<Session> sessions = sessionManager.getSessions(signature);
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