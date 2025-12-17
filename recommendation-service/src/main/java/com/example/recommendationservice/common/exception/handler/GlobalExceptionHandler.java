package com.example.recommendationservice.common.exception.handler;

import com.example.recommendationservice.common.exception.CustomException;
import com.example.recommendationservice.common.exception.status.ResponseStatusCode;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.recommendationservice.common.exception.mapper.CommonResponseMapper.getErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseDto<Empty>> handleCustomException(CustomException ex) {
        ResponseStatusCode responseStatusCode = ex.getStatus();

        return ResponseEntity
            .status(responseStatusCode.getHttpStatusCode())
            .body(getErrorResponse(responseStatusCode));
    }

}
