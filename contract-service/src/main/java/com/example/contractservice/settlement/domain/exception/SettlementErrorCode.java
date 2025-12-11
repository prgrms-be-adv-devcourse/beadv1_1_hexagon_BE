package com.example.contractservice.settlement.domain.exception;

import com.example.contractservice.common.domain.exception.DomainErrorCode;
import org.springframework.http.HttpStatus;

public class SettlementErrorCode extends DomainErrorCode {
    public static final SettlementErrorCode FEE_NOT_CALCULATED;
    public static final DomainErrorCode SETTLEMENT_NOT_EXISTS;

    static {
        FEE_NOT_CALCULATED = new SettlementErrorCode(HttpStatus.BAD_REQUEST, 4200, "정산 수수료가 계산되지 않았습니다.");
        SETTLEMENT_NOT_EXISTS = new SettlementErrorCode(HttpStatus.BAD_REQUEST, 4201, "해당하는 정산 데이터가 존재하지 않습니다.");
    }

    private SettlementErrorCode(HttpStatus httpStatusCode, int statusCode, String message) {
        super(httpStatusCode, statusCode, message);
    }
}
