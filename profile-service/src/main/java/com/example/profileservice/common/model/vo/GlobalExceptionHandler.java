package com.example.profileservice.common.model.vo;

import static com.example.profileservice.common.model.vo.ResponseDtoMapper.getErrorResponse;

import com.example.profileservice.common.model.vo.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 모든 Controller에서 발생하는 예외를 처리
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 로직 예외 (CustomException) 처리
     */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ResponseDto<Empty>> handleCustomException(CustomException e) {
        log.error("CustomException Occurred: {} - {}", e.getErrorCode().getCode(), e.getMessage());

        ErrorCode errorCode = e.getErrorCode();

        // CustomException을 ResponseDto의 실패 형태로 변환하여 반환
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(getErrorResponse(errorCode, e.getMessage()));
    }

    /**
     * @Valid 또는 @Validated를 사용한 입력 값 유효성 검사 실패 시 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ResponseDto<Empty>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException Occurred: {}", e.getMessage());

        final ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        // 유효하지 않은 필드와 메시지를 조합하여 상세 에러 메시지 생성
        final String detailedMessage = String.format("%s (Field: %s)",
                e.getBindingResult().getFieldError().getDefaultMessage(),
                e.getBindingResult().getFieldError().getField());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(getErrorResponse(errorCode, detailedMessage));
    }

    /**
     * 404 Not Found (요청 URI에 해당하는 핸들러가 없을 때)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<ResponseDto<Empty>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("NoHandlerFoundException Occurred: {}", e.getMessage());

        final ErrorCode errorCode = ErrorCode.NOT_FOUND_RESOURCE;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(getErrorResponse(errorCode, errorCode.getMessage() + " - " + e.getRequestURL()));
    }


    /**
     * 기타 예상치 못한 모든 서버 예외 처리 (500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ResponseDto<Empty>> handleException(Exception e) {
        log.error("Unexpected Exception Occurred: {}", e.getMessage(), e);

        final ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(getErrorResponse(errorCode, errorCode.getMessage()));
    }
}
