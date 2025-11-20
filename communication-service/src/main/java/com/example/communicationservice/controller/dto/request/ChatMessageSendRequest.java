package com.example.communicationservice.controller.dto.request;

public record ChatMessageSendRequest(
    String roomId,
    String senderCode,
    String content
) {
}
