package com.example.contractservice.contract.common;

import com.example.contractservice.contract.domain.exception.ContractErrorCode;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;

public abstract class ResponseDtoMapper {

    private ResponseDtoMapper() {
    }

    public static ResponseDto<Empty> getErrorResponse(ContractErrorCode errorCode) {
        return new ResponseDto<>(errorCode.getStatusCode(),
                errorCode.getHttpStatusCode(),
                errorCode.getMessage(),
                Empty.getInstance());
    }
}
