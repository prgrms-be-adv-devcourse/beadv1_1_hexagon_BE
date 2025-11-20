package com.example.communicationservice.controller.dto.response;

import com.example.communicationservice.entity.ChatMessage;

import java.time.Instant;

public record ChatMessageReadResponse(
    String id,
    String senderCode,
    String content,
    Instant sentAt
) {
    public static ChatMessageReadResponse from(ChatMessage message) {
        return new ChatMessageReadResponse(
            message.getId(),
            message.getSenderCode(),
            message.getContent(),
            message.getSentAt()
        );
    }
}
