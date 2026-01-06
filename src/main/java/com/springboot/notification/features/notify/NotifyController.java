package com.springboot.notification.features.notify;

import com.springboot.lib.helper.ControllerHelper;
import com.springboot.notification.route.RouteConstant;
import com.springboot.notification.service.notify.NotifyService;
import com.springboot.notification.service.notify.data.ENotifyMethod;
import com.springboot.notification.service.notify.data.ETemplateNotify;
import com.springboot.notification.service.notify.dto.NotifyRequest;
import com.springboot.notification.service.notify.NotifyBuilder;
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
        notifyRequest.setTitle("[UAT] OTP");
        notifyRequest.setContent(NotifyBuilder.build(ETemplateNotify.OTP.getTemplateId(), "133111"));
        notifyRequest.setEmail("dinhducn10@gmail.com");
        return ControllerHelper.success(notifyService.sendNotify(notifyRequest));
    }
}
