package com.example.communicationservice.controller.dto.response;

import com.example.communicationservice.entity.ChatRoom;

public record ChatRoomCreateResponse(
    String id
) {
    public static ChatRoomCreateResponse from(ChatRoom chatRoom) {
        return new ChatRoomCreateResponse(chatRoom.getId());
    }
}
