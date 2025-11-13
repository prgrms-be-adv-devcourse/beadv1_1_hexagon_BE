package com.example.searchservice.selfpromotion.exception;

import com.example.searchservice.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SelfPromotionErrorCode implements ErrorCode {
    SELF_PROMOTION_FETCH_FAILED(5101, 500, "Self Promotion 불러오기 실패");

    private final int code;
    private final int httpStatus;
    private final String message;
}
