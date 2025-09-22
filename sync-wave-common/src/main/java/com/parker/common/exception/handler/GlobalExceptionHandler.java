package com.parker.common.exception.handler;

import com.parker.common.exception.CustomException;
import com.parker.common.resonse.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({CustomException.class})
    public ResponseEntity<CommonResponse<?>> handleCustomException(CustomException exception) {
        CommonResponse<?> commonResponse = new CommonResponse<>(exception.getErrorCode(), exception.getMessage());
        return ResponseEntity.status(exception.getHttpStatus()).body(commonResponse);
    }

    @ExceptionHandler({IOException.class})
    public ResponseEntity<CommonResponse<?>> handleIOEException(CustomException exception) {
        CommonResponse<?> commonResponse = new CommonResponse<>(exception.getErrorCode(), exception.getMessage());
        return ResponseEntity.status(exception.getHttpStatus()).body(commonResponse);
    }
}
