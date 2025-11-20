package com.example.communicationservice.controller.dto.response;

import java.util.List;

public record ChatMessageListReadResponse(
    List<ChatMessageReadResponse> messages,
    PageInfo pageInfo
) {
}
