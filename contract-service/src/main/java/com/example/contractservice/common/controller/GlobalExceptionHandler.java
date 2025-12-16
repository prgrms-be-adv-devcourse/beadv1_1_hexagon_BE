package com.example.contractservice.common.controller;

import com.example.contractservice.common.util.StringUtil;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.hexagon.core.dto.ResponseDto;
import org.hexagon.core.dto.Empty;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseDto<Empty> handle(MethodArgumentNotValidException e) {
        String allExceptionMessages = e.getBindingResult().getAllErrors().stream().map(err -> err.getDefaultMessage())
                .collect(Collectors.joining(" | "));

        String message = StringUtil.format("입력 값이 올바르지 않습니다. {}", allExceptionMessages);

        return new ResponseDto<>(4999, HttpStatus.BAD_REQUEST.value(), message, Empty.getInstance());
    }

    @ExceptionHandler(BindException.class)
    public ResponseDto<Empty> handle(BindException e) {
        String bindingFailObjects = e.getBindingResult().getAllErrors().stream().map(err -> err.getObjectName())
                .collect(Collectors.joining(", "));

        String message = StringUtil.format("잘못된 타입의 입력이 존재합니다. 다음을 확인하십시오: {}", bindingFailObjects);

        return new ResponseDto<>(4999, HttpStatus.BAD_REQUEST.value(), message, Empty.getInstance());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseDto<Empty> handle() {
        String message = "잘못된 타입의 입력이 있거나 입력 구조가 잘못되었습니다.";

        return new ResponseDto<>(4999, HttpStatus.BAD_REQUEST.value(), message, Empty.getInstance());
    }

    @ExceptionHandler(Exception.class)
    public ResponseDto<Empty> handle(Exception e) {
        log.error("알 수 없는 예외 발생. 빠른 확인 필요!", e);

        return ResponseDto.fail();
    }
}
