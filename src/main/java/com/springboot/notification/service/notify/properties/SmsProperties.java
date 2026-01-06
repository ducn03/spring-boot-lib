package com.springboot.notification.service.notify.properties;

import com.springboot.notification.service.notify.dto.NotifyRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.sms")
public class SmsProperties extends ZnsSmsProperties {

    private String fromSms;
    private Boolean unicode;
    private Integer contentId;
    private String smsEndpoint;

    /**
     * Build SMS payload for direct SMS sending
     */
    public Map<String, Object> buildPayload(NotifyRequest request) {
        validatePhoneNumber(request.getPhone());

        Map<String, Object> payload = new HashMap<>();
        payload.put("from", fromSms);
        payload.put("to", convertToInternationalFormat(request.getPhone()));
        payload.put("text", request.getContent());

        // Add optional SMS properties if configured
        if (unicode != null) {
            payload.put("unicode", unicode ? 1 : 0);
        }
        if (contentId != null) {
            payload.put("contentid", contentId);
        }

        return payload;
    }
}