package com.example.contractservice.settlement.domain.exception;

import com.example.contractservice.common.domain.exception.DomainErrorCode;
import org.springframework.http.HttpStatus;

public class SettlementErrorCode extends DomainErrorCode {
    public static final SettlementErrorCode FEE_NOT_CALCULATED;

    static {
        FEE_NOT_CALCULATED = new SettlementErrorCode(HttpStatus.BAD_REQUEST, 4200, "정산 수수료가 계산되지 않았습니다.");
    }

    private SettlementErrorCode(HttpStatus httpStatusCode, int statusCode, String message) {
        super(httpStatusCode, statusCode, message);
    }
}
