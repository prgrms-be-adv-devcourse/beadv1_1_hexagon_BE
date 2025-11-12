package com.example.searchservice.tag.exception;

import com.example.searchservice.common.exception.BaseException;
import com.example.searchservice.common.exception.ErrorCode;

public class TagException extends BaseException {

    public TagException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TagException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
