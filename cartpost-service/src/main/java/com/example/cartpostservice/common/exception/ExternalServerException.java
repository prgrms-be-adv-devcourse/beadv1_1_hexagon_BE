package com.example.cartpostservice.common.exception;

import lombok.Getter;

@Getter
public class ExternalServerException extends RuntimeException{

    private final CustomStatusCode customStatusCode;

    public ExternalServerException(CustomStatusCode customStatusCode, String externalMessage){
        super(String.format("code : %d  internal Message: %s ExternalMessage: %s",
                customStatusCode.getCode(),
                customStatusCode.getMessage(),
                (externalMessage != null && !externalMessage.isBlank()) ? externalMessage : "No sending external message" ) );

        this.customStatusCode = customStatusCode;
    }

}
