package com.example.profileservice.common.model.vo;

import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;

public abstract class ResponseDtoMapper {

    private ResponseDtoMapper() {
    }

    public static ResponseDto<Empty> getErrorResponse(ErrorCode errorCode, String message) {
        return new ResponseDto<>(
                errorCode.getCode(),
                errorCode.getStatus().value(),
                message,
                Empty.getInstance());
    }
}
