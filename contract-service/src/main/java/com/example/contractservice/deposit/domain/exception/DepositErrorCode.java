package com.example.contractservice.deposit.domain.exception;

import org.springframework.http.HttpStatus;

public enum DepositErrorCode {
    NO_DEPOSIT_ENTITY(HttpStatus.BAD_REQUEST, 4100, "해당하는 예치금이 존재하지 않습니다."),
    NOT_ENOUGH_AMOUNT(HttpStatus.BAD_REQUEST, 4101, "예치금 잔액이 부족합니다."),
    INVALID_AMOUNT(HttpStatus.BAD_REQUEST, 4102, "처리 금액이 잘못되었습니다."),

    ALREADY_EXISTS(HttpStatus.BAD_REQUEST, 4110, "이미 존재하는 예치금입니다.");

    private final int httpStatusCode;
    private final int statusCode;
    private final String message;

    DepositErrorCode(HttpStatus httpStatusCode, int statusCode, String message) {
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
