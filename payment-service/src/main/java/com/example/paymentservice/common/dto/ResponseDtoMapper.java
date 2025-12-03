package com.example.paymentservice.common.dto;

import com.example.paymentservice.common.dto.enums.CustomStatusCode;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;

public abstract class ResponseDtoMapper {

    private ResponseDtoMapper() {
    }

    public static ResponseDto<Empty> getErrorResponse(CustomStatusCode customStatusCode) {
        return new ResponseDto<>(customStatusCode.getCode(),
                customStatusCode.getStatus().value(),
                customStatusCode.getMessage(),
                Empty.getInstance());
    }
}
