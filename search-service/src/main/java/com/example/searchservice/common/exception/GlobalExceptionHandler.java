package com.example.searchservice.common.exception;

import com.example.searchservice.common.response.BaseResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public BaseResponse<Void> handleException(BaseException e) {
        ErrorCode errorCode = e.getErrorCode();
        return new BaseResponse<>(
                errorCode.getStatusCode(),
                errorCode.getMessage(),
                null
        );
    }
}
