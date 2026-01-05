package com.springboot.notification.service.notify.templates;

import com.springboot.notification.service.notify.data.ETemplateNotify;
import com.springboot.notification.utils.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class NotifyBuilder {

    /**
     *
     * @param templateId - ETemplateNotify
     * @param content - NotifyRequest
     * @return
     */
    public static String build(int templateId, String content) {
        if (ETemplateNotify.OTP.getTemplateId() == templateId) {
            return getOtpContent(content);
        }

        return null;
    }

    private static String getOtpContent(String content) {
        if (StringUtils.isNullOrEmpty(content)) {
            return null;
        }
        return String.format("Ma OTP cua ban la %s. Vui long khong chia se ma nay voi bat ky ai.", content);
    }
}
