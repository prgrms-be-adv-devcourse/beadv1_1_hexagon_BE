package com.example.communicationservice.controller.dto.request;

import com.example.communicationservice.controller.dto.FileInfo;
import com.example.communicationservice.type.MessageType;
import jakarta.validation.constraints.NotNull;

public record ChatMessageSendRequest(

    @NotNull
    String roomId,

    @NotNull
    String senderCode,

    @NotNull
    MessageType type,

    String text,

    FileInfo file

) {
}
