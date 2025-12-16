package com.example.cartpostservice.common.exception;

import static com.example.cartpostservice.common.model.dto.ResponseDtoMapper.getErrorResponse;

import feign.FeignException;
import feign.RetryableException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ExceptionLogService exceptionLogService;

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ResponseDto<Empty>> handleBusinessException(BusinessException ex) {
        log.error("handleBusinessException: {}", ex.getMessage());

        CustomStatusCode customStatusCode = ex.getCustomStatusCode();
        ResponseDto<Empty> response = getErrorResponse(customStatusCode);

        return new ResponseEntity<>(response, customStatusCode.getStatus());
    }

    @ExceptionHandler({
            FeignException.class,
            RetryableException.class,
            ConnectException.class,
            SocketTimeoutException.class,
            ExternalServerException.class,})
    public ResponseEntity<ResponseDto<Empty>> handleExternalServerException(Exception ex) {
        CustomStatusCode errorCode = exceptionLogService.logExternalServerException(ex, "GlobalExceptionHandler");

        return new ResponseEntity<>(getErrorResponse(errorCode), errorCode.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ResponseDto<Empty>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        log.error("handleMethodArgumentNotValidException: {}", ex.getMessage());

        BindingResult bindingResult = ex.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();

        CustomStatusCode errorCode = CustomStatusCode.BAD_REQUEST_PARAMETER;
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : errorCode.getMessage();

        return new ResponseEntity<>(getErrorResponse(errorCode, errorMessage), errorCode.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseDto<Empty>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("handleConstraintViolationException: {}, ", ex.getMessage());

        ConstraintViolation<?> violation = ex.getConstraintViolations().iterator().next();
        CustomStatusCode errorCode = CustomStatusCode.BAD_REQUEST_PARAMETER;
        String errorMessage = violation != null ? violation.getMessage() : errorCode.getMessage();

        return new ResponseEntity<>(getErrorResponse(errorCode, errorMessage), errorCode.getStatus());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ResponseDto<Empty>> handleGeneralException(Exception ex) {
        log.error("handleGeneralException: {}", ex.getMessage(), ex);

        CustomStatusCode errorCode = CustomStatusCode.INTERNAL_SERVER_ERROR;

        return new ResponseEntity<>(getErrorResponse(errorCode), errorCode.getStatus());
    }


}
