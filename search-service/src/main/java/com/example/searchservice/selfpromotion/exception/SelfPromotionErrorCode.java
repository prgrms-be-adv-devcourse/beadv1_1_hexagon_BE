package com.example.searchservice.selfpromotion.exception;

import com.example.searchservice.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SelfPromotionErrorCode implements ErrorCode {
    SELF_PROMOTION_PAY_FILTER_ERROR(5101, HttpStatus.BAD_REQUEST.value(), "max-pay를 지정하기 위해서는 먼저 payment-type을 지정해야 합니다.");

    private final int code;
    private final int httpStatus;
    private final String message;
}
