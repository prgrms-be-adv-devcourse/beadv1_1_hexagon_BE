package com.example.contractservice.common.domain.exception;

public class DomainException extends RuntimeException {
    protected final DomainErrorCode errorCode;

    public DomainException(DomainErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public DomainErrorCode getErrorCode() {
        return errorCode;
    }
}
