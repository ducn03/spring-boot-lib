package com.springboot.notification.service.notify.channels;

import com.springboot.notification.service.notify.MessageSender;
import com.springboot.notification.service.notify.dto.NotifyRequest;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailSender implements MessageSender {
    private final JavaMailSender mailSender;

    public EmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public boolean send(NotifyRequest notifyRequest) {
        try {
            log.info("Start send mail {} : {}", notifyRequest.getEmail(), notifyRequest.getSendTime());
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(notifyRequest.getEmail());
            helper.setSubject(notifyRequest.getTitle());
            helper.setText(notifyRequest.getContent(), true);

            mailSender.send(message);
            log.info("Send mail success {} : {}", notifyRequest.getEmail(), notifyRequest.getSendTime());
        } catch (Exception ignore) {
            log.error("Send email fail {} : {}", notifyRequest.getEmail(), notifyRequest.getSendTime());
        }
        return true;
    }

    @Override
    public long getBalance() {
        return 0;
    }
}
