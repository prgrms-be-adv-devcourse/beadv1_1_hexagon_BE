package com.example.contractservice.deposit.domain.exception;

import com.example.contractservice.common.domain.exception.DomainException;

public class DepositException extends DomainException {

    public DepositException(DepositErrorCode errorCode) {
        super(errorCode);
    }
}
