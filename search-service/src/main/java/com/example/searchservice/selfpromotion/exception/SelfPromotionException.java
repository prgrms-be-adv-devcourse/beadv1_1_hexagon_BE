package com.example.searchservice.selfpromotion.exception;

import com.example.searchservice.common.exception.BaseException;
import com.example.searchservice.common.exception.ErrorCode;

public class SelfPromotionException extends BaseException {

    public SelfPromotionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public SelfPromotionException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
