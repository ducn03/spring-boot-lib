package com.springboot.notification.service.notify.data;

/**
 * Template ID nên lớn hơn 1
 */
public enum ETemplateNotify {
    OTP(1);
    private final int templateId;

    ETemplateNotify(int templateId) {
        this.templateId = templateId;
    }

    public int getTemplateId() {
        return templateId;
    }

    public static ETemplateNotify fromValue(int templateId) {
        for (ETemplateNotify template : ETemplateNotify.values()) {
            if (template.templateId == templateId) {
                return template;
            }
        }
        return null;
    }
}
