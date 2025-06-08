package com.springboot.prj.ws.room;

import com.springboot.prj.ws.message.Message;
import jakarta.websocket.Session;

public interface RoomManager {
    void joinRoom(String roomId, String signature, Session session);
    void leaveRoom(String roomId, String signature);
    void sendMessage(String roomId, Message message);
}
