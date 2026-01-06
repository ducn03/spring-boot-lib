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
@ConfigurationProperties(prefix = "spring.zns")
public class ZnsProperties extends ZnsSmsProperties {

    // ZNS specific fields
    private String fromZns;
    private String otpTemplateId;
    private String confirmOrderTemplateId;
    private String getBalanceEndpoint;
    private String znsEndpoint;

    // Inherited from ZnsSmsProperties:
    // - username, password, endpoint
    // - All common methods

    /**
     * Build ZNS payload for notification request
     */
    public Map<String, Object> buildPayload(NotifyRequest request) {
        validatePhoneNumber(request.getPhone());

        String internationalPhone = convertToInternationalFormat(request.getPhone());
        Object templateData = prepareTemplateData(request);
        String smsText = prepareSmsText(request);

        Map<String, Object> payload = new HashMap<>();
        payload.put("from", fromZns);
        payload.put("to", internationalPhone);
        payload.put("template_id", request.getTemplateId());
        payload.put("template_data", templateData);
        payload.put("client_req_id", generateClientRequestId());
        payload.put("smsFailover", buildSmsFailover(internationalPhone, smsText));

        return payload;
    }

    private Object prepareTemplateData(NotifyRequest request) {
        if (confirmOrderTemplateId.equals(request.getTemplateId())) {
            return request.getContent();
        }
        return convertToOtpTemplate(request.getContent());
    }

    private String prepareSmsText(NotifyRequest request) {
        if (confirmOrderTemplateId.equals(request.getTemplateId())) {
            return buildConfirmOrderSms(request.getContent());
        }
        return extractOtpText(request.getContent());
    }

    private Map<String, Object> buildSmsFailover(String phone, String text) {
        Map<String, Object> smsFailover = new HashMap<>();
        // Use ZNS from for failover
        smsFailover.put("from", fromZns);
        smsFailover.put("to", phone);
        smsFailover.put("text", text);
        return smsFailover;
    }
}