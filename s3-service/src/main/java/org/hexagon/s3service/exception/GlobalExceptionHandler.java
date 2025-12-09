package org.hexagon.s3service.exception;

import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.awssdk.core.exception.SdkException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SdkException.class)
    public ResponseDto<Empty> handleException(SdkException e) {
        return new ResponseDto<>(
                5000,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "S3 SDK 오류가 발생했습니다.",
                Empty.getInstance()
        );
    }
}
