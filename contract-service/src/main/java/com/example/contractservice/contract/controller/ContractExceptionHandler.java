package com.example.contractservice.contract.controller;

import static com.example.contractservice.contract.common.ResponseDtoMapper.getErrorResponse;

import com.example.contractservice.contract.domain.exception.ContractException;
import com.example.contractservice.contract.domain.exception.ContractErrorCode;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ContractExceptionHandler {

    @ExceptionHandler(ContractException.class)
    public ResponseEntity<ResponseDto<Empty>> handleContractCreateException(ContractException e) {
        ContractErrorCode errorCode = e.getErrorCode();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorResponse(errorCode));
    }

}
