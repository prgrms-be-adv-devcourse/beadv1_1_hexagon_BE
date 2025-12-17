package com.example.recommendationservice.common.exception.mapper;

import com.example.recommendationservice.common.exception.status.ResponseStatusCode;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;

public abstract class CommonResponseMapper {

    private CommonResponseMapper() {}

    public static ResponseDto<Empty> getErrorResponse(ResponseStatusCode responseStatusCode) {
        return new ResponseDto<>(responseStatusCode.getCode(),
            responseStatusCode.getHttpStatusCode(),
            responseStatusCode.getMessage(),
            Empty.getInstance());
    }

    public static <T> ResponseDto<T> getErrorResponse(ResponseStatusCode responseStatusCode, T data) {
        return new ResponseDto<>(responseStatusCode.getCode(),
            responseStatusCode.getHttpStatusCode(),
            responseStatusCode.getMessage(),
            data);
    }

}
