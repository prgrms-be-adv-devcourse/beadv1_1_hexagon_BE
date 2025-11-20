package com.example.communicationservice.controller.dto.response;

import com.example.communicationservice.entity.ChatRoom;

import java.time.Instant;

public record ChatRoomReadResponse(
    String id,
    String name,
    Instant updatedAt
) {
    public static ChatRoomReadResponse from(ChatRoom chatRoom) {
        return new ChatRoomReadResponse(
            chatRoom.getId(),
            chatRoom.getName(),
            chatRoom.getUpdatedAt()
        );
    }
}
