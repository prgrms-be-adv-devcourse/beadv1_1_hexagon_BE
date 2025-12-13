package com.example.contractservice.deposit.domain.exception;

import com.example.contractservice.common.domain.exception.DomainErrorCode;
import org.springframework.http.HttpStatus;

public class DepositErrorCode extends DomainErrorCode {
    public static final DepositErrorCode NO_DEPOSIT_ENTITY;
    public static final DepositErrorCode NOT_ENOUGH_AMOUNT;
    public static final DepositErrorCode INVALID_AMOUNT;

    public static final DepositErrorCode ALREADY_EXISTS;

    public static final DepositErrorCode NO_HISTORY_ENTITY;

    static {
        NO_DEPOSIT_ENTITY = new DepositErrorCode(HttpStatus.BAD_REQUEST, 4100, "해당하는 예치금이 존재하지 않습니다.");
        NOT_ENOUGH_AMOUNT = new DepositErrorCode(HttpStatus.BAD_REQUEST, 4101, "예치금 잔액이 부족합니다.");
        INVALID_AMOUNT = new DepositErrorCode(HttpStatus.BAD_REQUEST, 4102, "처리 금액이 잘못되었습니다.");
        ALREADY_EXISTS = new DepositErrorCode(HttpStatus.BAD_REQUEST, 4110, "이미 존재하는 예치금입니다.");
        NO_HISTORY_ENTITY = new DepositErrorCode(HttpStatus.BAD_REQUEST, 4120, "해당하는 예치금 내역이 존재하지 않습니다.");
    }

   private DepositErrorCode(HttpStatus httpStatusCode, int statusCode, String message) {
        super(httpStatusCode, statusCode, message);
    }
}
