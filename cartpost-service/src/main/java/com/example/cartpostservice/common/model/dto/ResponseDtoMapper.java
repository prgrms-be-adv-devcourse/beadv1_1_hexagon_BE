package com.example.cartpostservice.common.model.dto;

import com.example.cartpostservice.common.exception.CustomStatusCode;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;

public abstract class ResponseDtoMapper {

    private ResponseDtoMapper() {
    }

    public static <T> ResponseDto<T> getSuccessResponse(CustomStatusCode customStatusCode, T data) {
        return new ResponseDto<>(customStatusCode.getCode(),
                customStatusCode.getStatus().value(),
                customStatusCode.getMessage(),
                data);
    }

    public static ResponseDto<Empty> getErrorResponse(CustomStatusCode customStatusCode) {
        return new ResponseDto<>(customStatusCode.getCode(),
                customStatusCode.getStatus().value(),
                customStatusCode.getMessage(),
                Empty.getInstance());
    }

    public static ResponseDto<Empty> getErrorResponse(CustomStatusCode customStatusCode, String customMessage) {
        return new ResponseDto<>(customStatusCode.getCode(),
                customStatusCode.getStatus().value(),
                customMessage,
                Empty.getInstance());
    }
}
