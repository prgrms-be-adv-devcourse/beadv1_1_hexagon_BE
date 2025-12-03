package com.example.contractservice.contract.domain.exception;

import com.example.contractservice.common.domain.exception.DomainException;

public class ContractException extends DomainException {

    public ContractException(ContractErrorCode errorCode) {
        super(errorCode);
    }
}
