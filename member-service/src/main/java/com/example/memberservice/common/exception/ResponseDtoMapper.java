package com.example.memberservice.common.exception;

import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;

public abstract class ResponseDtoMapper {

    private ResponseDtoMapper() {}

    public static ResponseDto<Empty> getErrorResponse(ErrorCode errorCode) {
        return new ResponseDto<>(errorCode.getCode(),
                errorCode.getHttpStatusCode(),
                errorCode.getMessage(),
                Empty.getInstance());
    }

    public static ResponseDto<Empty> getErrorResponse(ErrorCode errorCode, String message) {
        return new ResponseDto<>(errorCode.getCode(),
                errorCode.getHttpStatusCode(),
                message,
                Empty.getInstance());
    }

}
