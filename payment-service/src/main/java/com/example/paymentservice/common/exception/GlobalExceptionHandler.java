package com.example.paymentservice.common.exception;

import static com.example.paymentservice.common.dto.ResponseDtoMapper.getErrorResponse;

import com.example.paymentservice.common.dto.enums.CustomStatusCode;
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
//    @ExceptionHandler(Exception.class)
//    protected ResponseEntity<ResponseDto<EmptyDto>> handleGeneralException(Exception ex) {
//        log.error("handleGeneralException: {}", ex.getMessage(), ex); // 스택 트레이스 로깅
//
//        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
//        ResponseDto response = ResponseDto.of(errorCode);
//
//        return new ResponseEntity<>(response, errorCode.getStatus());
//    }
}
