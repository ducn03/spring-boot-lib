package com.springboot.prj.features.notify;

import com.springboot.lib.service.controller.ControllerService;
import com.springboot.prj.ws.SocketServer;
import com.springboot.prj.ws.message.NotifyMessage;
import com.springboot.prj.ws.room.RoomType;
import lombok.CustomLog;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Component
@CustomLog
public class NotifyController {
    private final ControllerService controllerService;
    private final SocketServer socketServer;

    public NotifyController(ControllerService controllerService, SocketServer socketServer) {
        this.controllerService = controllerService;
        this.socketServer = socketServer;
    }

    public ServerResponse sendNoti(ServerRequest request){
        log.info("start send message");
        NotifyMessage message = new NotifyMessage();
        message.setNotifyType("notify");
        message.setContent("Duc dep trai");
        message.setTimestamp(System.currentTimeMillis());

        socketServer.joinRoom(RoomType.NOTIFY, "notify", "user123");
        socketServer.sendMessage(RoomType.NOTIFY, "notify", message);
        log.info("send socket message");
        return controllerService.success(null);
    }

}
