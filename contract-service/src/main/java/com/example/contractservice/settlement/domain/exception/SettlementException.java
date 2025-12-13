package com.example.contractservice.settlement.domain.exception;

import com.example.contractservice.common.domain.exception.DomainErrorCode;
import com.example.contractservice.common.domain.exception.DomainException;

public class SettlementException extends DomainException {

    public SettlementException(DomainErrorCode errorCode) {
        super(errorCode);
    }
}
