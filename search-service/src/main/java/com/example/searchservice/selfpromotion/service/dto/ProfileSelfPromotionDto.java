package com.example.searchservice.selfpromotion.service.dto;

import java.time.Instant;

public record ProfileSelfPromotionDto(
        String code,
        String title,
        String content,
        String memberCode,
        String memberNickname,
        Instant updatedAt
) {

}
