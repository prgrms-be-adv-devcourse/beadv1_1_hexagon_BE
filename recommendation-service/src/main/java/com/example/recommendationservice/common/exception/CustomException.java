package com.example.recommendationservice.common.exception;

import com.example.recommendationservice.common.exception.status.ResponseStatusCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ResponseStatusCode status;

    public CustomException(ResponseStatusCode status) {
        super(status.getMessage());
        this.status = status;
    }

}
