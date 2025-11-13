package com.example.searchservice.selfpromotion.dto;

import java.time.Instant;

public record SelfPromotionDto(
        String code,
        String title,
        String content,
        String memberNickname,
        Instant updatedAt
) {

}
