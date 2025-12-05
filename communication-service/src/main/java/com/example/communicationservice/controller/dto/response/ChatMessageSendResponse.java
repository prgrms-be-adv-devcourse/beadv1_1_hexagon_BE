package com.example.communicationservice.controller.dto.response;

import com.example.communicationservice.controller.dto.FileInfo;
import com.example.communicationservice.entity.ChatMessage;
import com.example.communicationservice.type.MessageType;

import java.time.Instant;

public record ChatMessageSendResponse(
    String messageId,
    String roomId,
    String senderCode,
    MessageType type,
    String text,
    FileInfo file,
    Instant sentAt
) {
    public static ChatMessageSendResponse from(ChatMessage message) {
        return new ChatMessageSendResponse(
            message.getId(),
            message.getRoomId(),
            message.getSenderCode(),
            message.getType(),
            message.getText(),
            FileInfo.from(message.getFile()),
            message.getSentAt()
        );
    }
}
