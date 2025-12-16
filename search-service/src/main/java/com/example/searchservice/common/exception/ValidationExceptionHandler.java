package com.example.searchservice.common.exception;

import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.hexagon.core.dto.ResponseDto;
import org.hexagon.core.dto.Empty;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseDto<Empty> handleException(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));

        return new ResponseDto<>(
                5000,
                HttpStatus.BAD_REQUEST.value(),
                message,
                Empty.getInstance()
        );
    }
}
