package com.example.communicationservice.common.exception;

import com.example.communicationservice.common.status.ResponseDtoStatus;

public class ChatRoomException extends CustomException {

    public ChatRoomException(ResponseDtoStatus status) {
        super(status);
    }

}
