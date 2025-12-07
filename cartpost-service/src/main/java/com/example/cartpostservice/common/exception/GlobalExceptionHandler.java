package com.example.cartpostservice.common.exception;

import static com.example.cartpostservice.common.model.dto.ResponseDtoMapper.getErrorResponse;

import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
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

    @ExceptionHandler(ExternalServerException.class)
    public ResponseEntity<ResponseDto<Empty>> handleExternalServerException(ExternalServerException ex) {
        log.warn("Feign Network Error : {}", ex.getMessage());

        CustomStatusCode errorCode = CustomStatusCode.EXTERNAL_SERVER_ERROR;

        return new ResponseEntity<>(getErrorResponse(errorCode) , errorCode.getStatus());

    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ResponseDto<Empty>> handleGeneralException(Exception ex) {
        log.error("handleGeneralException: {}", ex.getMessage(), ex);

        CustomStatusCode errorCode = CustomStatusCode.INTERNAL_SERVER_ERROR;

        return new ResponseEntity<>(getErrorResponse(errorCode), errorCode.getStatus());
    }

    //    @ExceptionHandler(MethodArgumentNotValidException.class)
//    protected ResponseEntity<ResponseDto<List<FieldErrorDetail>>> handleMethodArgumentNotValidException(
//            MethodArgumentNotValidException ex) {
//        log.warn("handleMethodArgumentNotValidException: {}", ex.getMessage());
//
//        BindingResult bindingResult = ex.getBindingResult();
//        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
//
//        // ResponseDto의 오버로딩된 of() 사용
//        ResponseDto<List<FieldErrorDetail>> response = ResponseDto.of(errorCode, bindingResult);
//
//        return new ResponseEntity<>(response, errorCode.getStatus());
//    }
//
//
}
