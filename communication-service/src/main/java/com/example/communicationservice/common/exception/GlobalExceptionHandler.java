package com.example.communicationservice.common.exception;

import static com.example.communicationservice.common.response.ResponseDtoMapper.getErrorResponse;

import com.example.communicationservice.common.status.ResponseDtoStatus;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 유효성 검사 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<List<String>>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .toList();

        return ResponseEntity
            .badRequest()
            .body(getErrorResponse(ResponseDtoStatus.VALIDATION_FAILED, errors));
    }

    // 커스텀 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseDto<Empty>> handleCustomException(CustomException ex) {
        ResponseDtoStatus status = ex.getStatus();

        return ResponseEntity
            .status(status.getHttpStatusCode())
            .body(getErrorResponse(status));
    }

}
