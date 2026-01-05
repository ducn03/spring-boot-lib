package com.springboot.ws.room;

import com.springboot.ws.message.Message;
import jakarta.websocket.Session;
import org.springframework.stereotype.Component;

@Component
public class ChatRoomManager implements RoomManager {
    @Override
    public void joinRoom(String roomId, String signature, Session session) {

    }

    @Override
    public void leaveRoom(String roomId, String signature) {

    }

    @Override
    public void sendMessage(String roomId, Message message) {

    }
}
