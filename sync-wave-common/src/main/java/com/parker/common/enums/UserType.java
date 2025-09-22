package com.parker.common.enums;

public enum UserType {
    ADMIN("ADMIN", "시스템 관리자"),
    MALL_OWNER("MALL_OWNER", "몰 점주"),
    MALL_CLIENT("MALL_CLIENT", "몰 사용자");

    UserType(String code, String value) {
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
