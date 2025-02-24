package com.springboot.lib.enums;

import lombok.Getter;

@Getter
public enum Status {
    ACTIVE(1, "Hoạt động"),
    DELETED(2, "Đã xóa"),
    INACTIVE(0, "Không hoạt động");

    private final int value;
    private final String message;

    Status(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public static Status fromValue(int value) {
        for (Status status : Status.values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }
}
