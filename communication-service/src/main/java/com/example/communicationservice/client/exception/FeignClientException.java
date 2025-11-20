package com.example.communicationservice.client.exception;

import com.example.communicationservice.common.exception.CustomException;
import com.example.communicationservice.common.status.ResponseDtoStatus;

public class FeignClientException extends CustomException {

    public FeignClientException(ResponseDtoStatus status) {
        super(status);
    }

}
