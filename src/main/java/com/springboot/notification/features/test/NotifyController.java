package com.springboot.notification.features.test;

import com.springboot.lib.helper.ControllerHelper;
import com.springboot.notification.route.RouteConstant;
import com.springboot.prj.ws.SocketServer;
import com.springboot.prj.ws.message.NotifyMessage;
import com.springboot.prj.ws.room.RoomType;
import lombok.CustomLog;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.function.ServerRequest;

@Component
@CustomLog
public class NotifyController {
    private final SocketServer socketServer;

    public NotifyController(SocketServer socketServer) {
        this.socketServer = socketServer;
    }

    @GetMapping(RouteConstant.APP.NOTIFY.NOTIFY)
    public ResponseEntity<?> sendNoti(ServerRequest request){
        log.info("start send message");
        NotifyMessage message = new NotifyMessage();
        message.setNotifyType("notify");
        message.setContent("Duc dep trai");
        message.setTimestamp(System.currentTimeMillis());

        socketServer.joinRoom(RoomType.NOTIFY, "notify", "user123");
        socketServer.sendMessage(RoomType.NOTIFY, "notify", message);
        log.info("send socket message");
        return ControllerHelper.success(null);
    }

}
