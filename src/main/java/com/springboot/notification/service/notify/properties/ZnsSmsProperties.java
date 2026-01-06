package com.springboot.notification.service.notify.properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.lib.exception.AppException;
import com.springboot.notification.exception.AppErrorCodes;
import com.springboot.notification.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Base properties class chứa common fields và methods cho ZNS & SMS
 */
@Getter
@Setter
@Slf4j
public abstract class ZnsSmsProperties {

    // Common constants
    protected static final String PHONE_PREFIX_VIETNAM = "84";
    protected static final String DATE_PATTERN = "ddMMyy";
    protected static final int OTP_VALIDITY_MINUTES = 2;

    // Common fields
    protected String username;
    protected String password;
    protected String endpoint;
    protected String websiteUrl;
    protected String brandName;
    protected String getBalanceEndpoint;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Generate Base64 encoded authentication string
     */
    public String getAuth() {
        String credentials = username + ":" + password;
        return Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    /**
     * Validate phone number
     */
    protected void validatePhoneNumber(String phone) {
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new AppException(AppErrorCodes.NOTIFY.PHONE_IS_EMPTY);
        }
    }

    /**
     * Convert phone number to international format
     */
    protected String convertToInternationalFormat(String phoneNumber) {
        if (phoneNumber.startsWith("0")) {
            return PHONE_PREFIX_VIETNAM + phoneNumber.substring(1);
        }
        return phoneNumber;
    }

    /**
     * Generate unique client request ID
     */
    protected String generateClientRequestId() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_PATTERN));
        return datePrefix + "_" + UUID.randomUUID();
    }

    /**
     * Convert message to OTP template format
     */
    protected Object convertToOtpTemplate(Object messageObject) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, String> messageMap = (Map<String, String>) messageObject;
            String otpCode = messageMap.get("message");

            Map<String, Object> template = new HashMap<>();
            template.put("otp", otpCode);

            return objectMapper.convertValue(template, Map.class);
        } catch (Exception e) {
            throw new AppException(AppErrorCodes.NOTIFY.INVALID_MESSAGE_FORMAT);
        }
    }

    public void handleApiResponse(HttpResponse<String> response) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(response.body());
        if (jsonNode.get("status").asInt() == 1) {
            log.info("ZNS API Response: " + response.body());
        } else {
            log.error("ZNS API call failed: " + response.body());
        }
    }
}