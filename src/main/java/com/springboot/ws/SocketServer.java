package com.springboot.ws;

import com.springboot.lib.cache.ILazyCache;
import com.springboot.lib.cache.LazyCache;
import com.springboot.lib.service.redis.Redis;
import com.springboot.ws.message.CartMessage;
import com.springboot.ws.message.ChatMessage;
import com.springboot.ws.message.Message;
import com.springboot.ws.message.NotifyMessage;
import com.springboot.ws.room.*;
import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;
import lombok.CustomLog;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@CustomLog
public class SocketServer {
    private final SessionManager sessionManager;
    private final Object lock = new Object();
    private final Redis redis;
    private final ILazyCache<LazyAnalytics> analyticsCache;
    private final Map<RoomType, RoomManager> roomManagers;

    public SocketServer(Redis redis, CartRoomManager cartRoomManager,
                        ChatRoomManager chatRoomManager, NotifyRoomManager notifyRoomManager, SessionManager sessionManager) {
        this.redis = redis;
        this.sessionManager = sessionManager;
        this.roomManagers = new HashMap<>();
        this.roomManagers.put(RoomType.CART, cartRoomManager);
        this.roomManagers.put(RoomType.CHAT, chatRoomManager);
        this.roomManagers.put(RoomType.NOTIFY, notifyRoomManager);
        this.analyticsCache = new LazyCache<>(30000) {
            @Override
            public LazyAnalytics load() {
                log.info("Số session: " + sessionManager.getSessionCount());
                return new LazyAnalytics();
            }
        };
        SocketManager.getInstance().setListener(initSocketListener());
    }

    private SocketListener initSocketListener() {
        return new SocketListener() {
            @Override
            public void onOpen(Session session) {
                analyticsCache.get();
                String signature = session.getQueryString();
                if (signature == null || signature.isEmpty()) {
                    try {
                        session.close();
                    } catch (IOException ignored) {
                    }
                    return;
                }
                sessionManager.addSession(signature, session);
                log.info("Mở WebSocket: " + session.getId());
            }

            @Override
            public void onMessage(Session session, Message message) throws IOException {
                RoomManager roomManager = null;
                String roomId = null;

                if (message instanceof CartMessage cartMessage) {
                    roomManager = roomManagers.get(RoomType.CART);
                    roomId = cartMessage.getCartCode();
                } else if (message instanceof ChatMessage chatMessage) {
                    roomManager = roomManagers.get(RoomType.CHAT);
                    roomId = chatMessage.getReceiverId();
                } else if (message instanceof NotifyMessage notifyMessage) {
                    roomManager = roomManagers.get(RoomType.NOTIFY);
                    roomId = notifyMessage.getNotifyType();
                }

                if (roomManager != null && roomId != null) {
                    roomManager.sendMessage(roomId, message);
                } else {
                    log.info("Không tìm thấy RoomManager cho loại tin nhắn: " + message.getClass().getSimpleName());
                }
            }

            @Override
            public void onClose(Session session, CloseReason reason) {
                analyticsCache.get();
                CloseReason.CloseCode closeCode = reason != null ? reason.getCloseCode() : CloseReason.CloseCodes.NORMAL_CLOSURE;
                String reasonPhrase = reason != null && reason.getReasonPhrase() != null ? reason.getReasonPhrase() : "lý do không rõ";
                log.info("Đóng WebSocket " + session.getId() + " do " + reasonPhrase + " (mã: " + closeCode + ")");
                sessionManager.removeSession(session.getQueryString(), session); // Xóa session cụ thể
            }

            @Override
            public void onError(Session session, Throwable t) {
                log.info("Lỗi WebSocket session " + (session == null ? "null" : session.getId()));
                if (t instanceof SocketException) {
                    log.info("Kết nối reset ở session " + session.getId());
                    sessionManager.removeSession(session.getQueryString(), session); // Xóa session cụ thể
                }
            }
        };
    }

    public void joinRoom(RoomType roomType, String roomId, String signature, Session session) {
        RoomManager roomManager = roomManagers.get(roomType);
        if (roomManager != null) {
            roomManager.joinRoom(roomId, signature, session);
            log.info("Tham gia phòng " + roomId + " với signature " + signature);
        } else {
            log.info("Không tìm thấy RoomManager cho loại phòng: " + roomType);
        }
    }

    public void joinRoom(RoomType roomType, String roomId, String signature) {
        Set<Session> sessions = sessionManager.getSessions(signature); // Sửa để lấy tất cả session
        if (sessions.isEmpty()) {
            log.info("Không có session mở cho signature " + signature + " khi tham gia phòng " + roomId);
            return;
        }
        for (Session session : sessions) {
            if (session.isOpen()) {
                joinRoom(roomType, roomId, signature, session);
            }
        }
    }

    public void sendMessage(RoomType roomType, String roomId, Message message) {
        RoomManager roomManager = roomManagers.get(roomType);
        if (roomManager != null) {
            message.setType(roomType.name());
            roomManager.sendMessage(roomId, message);
        } else {
            log.info("Không tìm thấy RoomManager cho loại phòng: " + roomType);
        }
    }
}