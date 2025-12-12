package com.example.searchservice.commission.exception;

import com.example.searchservice.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommissionErrorCode implements ErrorCode {
    COMMISSION_PAY_FILTER_ERROR(5201, HttpStatus.BAD_REQUEST.value(), "min-pay를 지정하기 위해서는 먼저 payment-type을 지정해야 합니다."),
    COMMISSION_DATE_FILTER_ERROR(5202, HttpStatus.BAD_REQUEST.value(), "ended-at은 반드시 start-at보다 이후의 날짜여야 합니다.");

    private final int code;
    private final int httpStatus;
    private final String message;
}
