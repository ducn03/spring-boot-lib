package com.springboot.prj.ws;

import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;
import lombok.CustomLog;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@CustomLog
public class SessionManager {
    private final HashMap<String, Set<Session>> sessionHashMap = new HashMap<>();
    private final Object lock = new Object();

    public void addSession(String signature, Session session) {
        synchronized (lock) {
            sessionHashMap.computeIfAbsent(signature, k -> new HashSet<>()).add(session);
            log.info(String.format("Đã thêm phiên cho signature: %s", signature));
        }
    }

    public void removeSession(String signature, Session session) {
        synchronized (lock) {
            Set<Session> sessions = sessionHashMap.get(signature);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    sessionHashMap.remove(signature);
                }
                log.info(String.format("Đã xóa phiên cho signature: %s", signature));
            }
        }
    }

    public Set<Session> getSessions(String signature) {
        synchronized (lock) {
            return sessionHashMap.getOrDefault(signature, new HashSet<>());
        }
    }

    public int getSessionCount() {
        synchronized (lock) {
            return sessionHashMap.values().stream().mapToInt(Set::size).sum();
        }
    }

    public Map<String, Set<Session>> getAllSessions() {
        synchronized (lock) {
            return new HashMap<>(sessionHashMap);
        }
    }

    public void removeAllSession(String signature) {
        synchronized (lock) {
            Set<Session> sessions = sessionHashMap.remove(signature);
            if (sessions != null) {
                for (Session session : sessions) {
                    if (session.isOpen()) {
                        try {
                            session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Đăng xuất"));
                        } catch (IOException e) {
                            log.info("Lỗi khi đóng session " + session.getId() + ": " + e.getMessage());
                        }
                    }
                }
                log.info("Đã ngắt kết nối tất cả session cho signature: " + signature);
            }
        }
    }
}