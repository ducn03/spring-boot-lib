package com.springboot.lib.enums;

import lombok.Getter;

@Getter
public enum EStatus {
    ACTIVE(1, "Hoạt động"),
    DELETED(2, "Đã xóa"),
    INACTIVE(0, "Không hoạt động");

    private final int value;
    private final String message;

    EStatus(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public static EStatus fromValue(int value) {
        for (EStatus EStatus : EStatus.values()) {
            if (EStatus.value == value) {
                return EStatus;
            }
        }
        return null;
    }
}
