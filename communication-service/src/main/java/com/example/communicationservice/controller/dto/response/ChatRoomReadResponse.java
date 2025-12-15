package com.example.communicationservice.controller.dto.response;

import java.time.Instant;

public record ChatRoomReadResponse(
    String id,
    String name,
    Instant updatedAt
) {
}
