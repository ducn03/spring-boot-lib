package com.springboot.notification.features.notify;

import com.springboot.lib.helper.ControllerHelper;
import com.springboot.notification.route.RouteConstant;
import com.springboot.notification.service.notify.NotifyService;
import com.springboot.notification.service.notify.data.ENotifyMethod;
import com.springboot.notification.service.notify.dto.NotifyRequest;
import com.springboot.notification.service.notify.templates.NotifyBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotifyController {
    private final NotifyService notifyService;

    public NotifyController(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @PostMapping(RouteConstant.APP.NOTIFY.SEND)
    public ResponseEntity<?> send(){
        NotifyRequest notifyRequest = new NotifyRequest();
        notifyRequest.setMethod(ENotifyMethod.EMAIL);
        notifyRequest.setTitle("Test");
        notifyRequest.setContent(NotifyBuilder.build(1, "133111"));
        notifyRequest.setEmail("dinhducn10@gmail.com");
        return ControllerHelper.success(notifyService.sendMessage(notifyRequest));
    }
}
