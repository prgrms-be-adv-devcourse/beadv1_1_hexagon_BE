package com.example.communicationservice.controller.dto.response;

import java.util.List;

public record ChatRoomListReadResponse(
    List<ChatRoomReadResponse> chatRooms,
    PageInfo pageInfo
) {
}
