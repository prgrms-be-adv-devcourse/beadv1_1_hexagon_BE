package com.example.communicationservice.controller.dto.response;

import com.example.communicationservice.type.MessageType;

import java.time.Instant;

public record ChatMessageReadResponse(
    String id,
    String senderCode,
    MessageType type,
    String text,
    ChatFileReadResponse file,
    Instant sentAt
) {
}
