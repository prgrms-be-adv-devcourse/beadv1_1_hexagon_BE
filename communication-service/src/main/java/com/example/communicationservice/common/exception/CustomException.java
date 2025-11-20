package com.example.communicationservice.common.exception;

import com.example.communicationservice.common.status.ResponseDtoStatus;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ResponseDtoStatus status;

    public CustomException(ResponseDtoStatus status) {
        super(status.getMessage());
        this.status = status;
    }

}
