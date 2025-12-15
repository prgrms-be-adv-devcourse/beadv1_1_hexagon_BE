package com.example.memberservice.common.exception;

import static com.example.memberservice.common.web.ResponseDtoMapper.getErrorResponse;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseDto<Empty>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(getErrorResponse(errorCode));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Empty>> handleValidatorException(MethodArgumentNotValidException e) {

        String errorMessage = "Validation failed";

        if (!e.getBindingResult().getFieldErrors().isEmpty()) {
            FieldError fieldError = e.getBindingResult().getFieldErrors().get(0);
            errorMessage = "%s 잘못된 파라미터 : %s 전송값: %s".formatted(fieldError.getDefaultMessage(), fieldError.getField(),
                Optional.ofNullable(fieldError.getRejectedValue()).orElse("null").toString());
        }

        ResponseDto<Empty> response = getErrorResponse(ErrorCode.VALIDATION_FAILED, errorMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseDto<Empty>> handleEnumMismatch(MethodArgumentTypeMismatchException ex) {


        String paramName = ex.getName();
        String invalidValue = String.valueOf(ex.getValue());

        String errorMessage = "잘못된 " + paramName + " 값입니다: '" + invalidValue + "'";

        ResponseDto<Empty> response =
            getErrorResponse(ErrorCode.VALIDATION_FAILED, errorMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ResponseDto<Empty>> handleNotFound(NoHandlerFoundException ex) {

        ResponseDto<Empty> response = getErrorResponse(ErrorCode.NO_HANDLER);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResponseDto<Empty>> noResourceFound(NoResourceFoundException ex) {

        ResponseDto<Empty> response = getErrorResponse(ErrorCode.NO_HANDLER);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Empty>> handleException(Exception e) {
        return ResponseEntity.status(500).body(ResponseDto.fail());
    }
}
