package com.example.communicationservice.controller.dto.response;

import com.example.communicationservice.controller.dto.FileInfo;
import com.example.communicationservice.entity.ChatMessage;
import com.example.communicationservice.type.MessageType;

import java.time.Instant;

public record ChatMessageReadResponse(
    String id,
    String senderCode,
    MessageType type,
    String text,
    FileInfo file,
    Instant sentAt
) {
    public static ChatMessageReadResponse from(ChatMessage message) {
        return new ChatMessageReadResponse(
            message.getId(),
            message.getSenderCode(),
            message.getType(),
            message.getText(),
            FileInfo.from(message.getFile()),
            message.getSentAt()
        );
    }
}
