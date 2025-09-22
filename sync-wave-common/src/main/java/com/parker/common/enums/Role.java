package com.parker.common.enums;

public enum Role {
    ROLE_MASTER("ROLE_MASTER", "마스터"),
    ROLE_USER("ROLE_USER", "일반");

    Role(String code, String value) {
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
