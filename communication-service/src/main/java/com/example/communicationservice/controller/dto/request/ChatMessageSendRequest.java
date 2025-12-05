package com.example.communicationservice.controller.dto.request;

import com.example.communicationservice.controller.dto.FileInfo;
import com.example.communicationservice.entity.ChatMessage;
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
    public ChatMessage toEntity() {
        return ChatMessage.builder()
            .roomId(roomId)
            .senderCode(senderCode)
            .type(type)
            .text(text)
            .file(file != null ? file.toDomain() : null)
            .build();
    }
}
