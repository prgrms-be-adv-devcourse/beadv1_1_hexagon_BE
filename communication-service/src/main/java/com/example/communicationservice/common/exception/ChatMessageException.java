package com.example.communicationservice.common.exception;

import com.example.communicationservice.common.status.ResponseDtoStatus;

public class ChatMessageException extends CustomException {

    public ChatMessageException(ResponseDtoStatus status) {
        super(status);
    }

}
