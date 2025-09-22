package com.parker.common.resonse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResponse<T> {
    private boolean result;
    private T data;
    private String errorCode;
    private String errorMessage;

    public CommonResponse(T data) {
        this.result = true;
        this.data = data;
    }

    public CommonResponse(String errorCode, String errorMessage) {
        this.result = false;
        this.errorCode= errorCode;
        this.errorMessage= errorMessage;
    }
}
