package com.example.contractservice.common.controller;

import static com.example.contractservice.contract.common.ResponseDtoMapper.getErrorResponse;

import com.example.contractservice.common.domain.exception.DomainErrorCode;
import com.example.contractservice.contract.domain.exception.ContractException;
import com.example.contractservice.deposit.domain.exception.DepositException;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DomainExceptionHandler {

    @ExceptionHandler(ContractException.class)
    public ResponseEntity<ResponseDto<Empty>> handleContractException(ContractException e) {
        DomainErrorCode errorCode = e.getErrorCode();

        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(getErrorResponse(errorCode));
    }

    @ExceptionHandler(DepositException.class)
    public ResponseEntity<ResponseDto<Empty>> handleDepositException(DepositException e) {
        DomainErrorCode errorCode = e.getErrorCode();

        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(getErrorResponse(errorCode));
    }

}
