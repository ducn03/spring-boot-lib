package com.springboot.prj.ws;

public class SocketManager {

    private SocketListener listener;

    private static SocketManager instance;
    public static SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    private SocketManager() {

    }

    SocketListener getListener() {
        return this.listener;
    }

    void setListener(SocketListener listener) {
        this.listener = listener;
    }
}
