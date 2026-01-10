package com.springboot.notification.service.notify;

import com.springboot.lib.exception.AppException;
import com.springboot.notification.exception.AppErrorCodes;
import com.springboot.notification.service.notify.data.ENotifyMethod;
import com.springboot.notification.service.notify.data.ETemplateNotify;
import com.springboot.notification.service.notify.dto.NotifyRequest;
import com.springboot.notification.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

@Slf4j
@Service
public class NotifyService {
    private final MessageSenderFactory senderFactory;
    private final ExecutorService executorService;
    private final static long BALANCE_WARNING = 100000;

    public NotifyService(MessageSenderFactory senderFactory, ExecutorService executorService) {
        this.senderFactory = senderFactory;
        this.executorService = executorService;
    }

    public boolean sendNotify(NotifyRequest notifyRequest) {
        MessageSender sender = senderFactory.getSender(notifyRequest.getMethod());
        if (sender == null) {
            log.error("Unable to obtain an instance of Sender {} : {} : {}",
                    notifyRequest.getMethod(),
                    notifyRequest.getUserId(),
                    notifyRequest.getSendTime());
            throw new AppException(AppErrorCodes.SYSTEM.BAD_REQUEST);
        }

        buildContentIfNeed(notifyRequest);
        boolean result = sender.send(notifyRequest);

        if (result) {
            executorService.execute(() -> {
                handleSomethingAfterSendNotification(notifyRequest);
            });
        }

        return result;
    }

    /**
     * Xử lý những tác vụ này sau khi gửi thông báo
     */
    private void handleSomethingAfterSendNotification(NotifyRequest notifyRequest) {
        MessageSender sender = senderFactory.getSender(notifyRequest.getMethod());
        if (sender == null) {
            log.error("Unable to obtain an instance of Sender {} : {} : {}",
                    notifyRequest.getMethod(),
                    notifyRequest.getUserId(),
                    notifyRequest.getSendTime());
            throw new AppException(AppErrorCodes.SYSTEM.BAD_REQUEST);
        }

        // Xem noti này có cần lưu lại db ko
        if (notifyRequest.isSaveNotification()) {
            // TODO: Save data in db
        }

        // Ko phải phương thức nào cũng sẽ tính fee
        ENotifyMethod notifyMethod = notifyRequest.getMethod();
        if ((ENotifyMethod.ZALO == notifyMethod
                || ENotifyMethod.SMS == notifyMethod)
                && sender.getBalance() <= BALANCE_WARNING) {
            // TODO: Send notify or log v.v..
        }
    }

    /**
     * Nếu có templateId thì build content theo template có sẵn
     */
    private void buildContentIfNeed(NotifyRequest notifyRequest) {
        int templateId = notifyRequest.getTemplateId();
        if (templateId != ETemplateNotify.EMPTY.getTemplateId()) {
            String content = notifyRequest.getContent();
            content = NotifyBuilder.build(templateId, content);
            if (StringUtils.isNullOrEmpty(content)) {
                log.warn("Content is empty !!!");
            }
            notifyRequest.setContent(content);
        }
    }

}
