package com.parker.common.enums;

public enum UserStatus {
    ACTIVATED("ACTIVATED", "활성화"),
    HOLDING("HOLDING", "잠금상태"),
    WITHDRAWAL("WITHDRAWAL", "탈퇴");

    UserStatus(String code, String value) {
        this.code = code;
        this.value = value;
    }

    private final String code;
    private final String value;

    public String code() {
        return code;
    }

    public String value() {
        return value;
    }
}
