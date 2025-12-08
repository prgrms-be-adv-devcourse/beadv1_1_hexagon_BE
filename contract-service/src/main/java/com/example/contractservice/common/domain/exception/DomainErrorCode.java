package com.example.contractservice.common.domain.exception;

import org.springframework.http.HttpStatus;

public class DomainErrorCode {
    protected final int httpStatusCode;
    protected final int statusCode;
    protected final String message;

    protected DomainErrorCode (HttpStatus httpStatusCode, int statusCode, String message) {
        this.httpStatusCode = httpStatusCode.value();
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

}
