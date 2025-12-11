package com.example.searchservice.commission.exception;

import com.example.searchservice.common.exception.BaseException;
import com.example.searchservice.common.exception.ErrorCode;

public class CommissionException extends BaseException {

    public CommissionException(ErrorCode errorCode) { super(errorCode);}
    public CommissionException(ErrorCode errorCode, Throwable cause) { super(errorCode, cause);}
}
