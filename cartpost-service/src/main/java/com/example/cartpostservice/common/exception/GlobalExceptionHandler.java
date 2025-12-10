package com.example.cartpostservice.common.exception;

import static com.example.cartpostservice.common.model.dto.ResponseDtoMapper.getErrorResponse;

import feign.FeignException;
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
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ResponseDto<Empty>> handleBusinessException(BusinessException ex) {
        log.warn("handleBusinessException: {}", ex.getMessage());

        CustomStatusCode customStatusCode = ex.getCustomStatusCode();
        ResponseDto<Empty> response = getErrorResponse(customStatusCode);

        return new ResponseEntity<>(response, customStatusCode.getStatus());
    }

    @ExceptionHandler({FeignException.class, ExternalServerException.class})
    public ResponseEntity<ResponseDto<Empty>> handleExternalServerException(Exception ex) {
        log.warn("Feign Network Error : {}", ex.getMessage());

        CustomStatusCode errorCode = CustomStatusCode.EXTERNAL_SERVER_ERROR;

        return new ResponseEntity<>(getErrorResponse(errorCode), errorCode.getStatus());

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ResponseDto<Empty>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        log.warn("handleMethodArgumentNotValidException: {}", ex.getMessage());

        BindingResult bindingResult = ex.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();

        CustomStatusCode errorCode = CustomStatusCode.INVALID_REQUEST_PPARAMETER;
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : errorCode.getMessage();

        return new ResponseEntity<>(getErrorResponse(errorCode, errorMessage), errorCode.getStatus());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ResponseDto<Empty>> handleGeneralException(Exception ex) {
        log.error("handleGeneralException: {}", ex.getMessage(), ex);

        CustomStatusCode errorCode = CustomStatusCode.INTERNAL_SERVER_ERROR;

        return new ResponseEntity<>(getErrorResponse(errorCode), errorCode.getStatus());
    }


}
