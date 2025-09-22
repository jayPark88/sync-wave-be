package com.parker.common.enums;

public enum TodoStatus {
    PENDING("PENDING", "대기 중"),
    IN_PROGRESS("IN_PROGRESS", "진행 중"),
    COMPLETED("COMPLETED", "완료"),
    CANCELLED("CANCELLED", "취소");


    TodoStatus(String code, String value) {
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
