package com.example.communicationservice.common.response;

import com.example.communicationservice.common.status.ResponseDtoStatus;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;

public abstract class ResponseDtoMapper {

    private ResponseDtoMapper() {
    }

    public static ResponseDto<Empty> getErrorResponse(ResponseDtoStatus responseDtoStatus) {
        return new ResponseDto<>(responseDtoStatus.getCode(),
                responseDtoStatus.getHttpStatusCode(),
                responseDtoStatus.getMessage(),
                Empty.getInstance());
    }

    public static <T> ResponseDto<T> getErrorResponse(ResponseDtoStatus responseDtoStatus, T data) {
        return new ResponseDto<>(responseDtoStatus.getCode(),
                responseDtoStatus.getHttpStatusCode(),
                responseDtoStatus.getMessage(),
                data);
    }
}
