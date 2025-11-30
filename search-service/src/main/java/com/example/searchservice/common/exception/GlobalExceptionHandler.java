package com.example.searchservice.common.exception;

import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseDto<Empty> handleException(BaseException e) {
        ErrorCode errorCode = e.getErrorCode();
        return new ResponseDto<>(
                errorCode.getCode(),
                errorCode.getHttpStatus(),
                errorCode.getMessage(),
                Empty.getInstance()
        );
    }
}
