package com.example.searchservice.saga.events.selfpromotion;

import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;
import java.time.Instant;

public record SelfPromotionCreatedEvent(
        String code,
        String title,
        String content,
        String memberCode,
        String memberNickname,
        Instant updatedAt
) {
    public static SelfPromotionDocumentEntity toDocumentEntity(SelfPromotionCreatedEvent event) {
        return SelfPromotionDocumentEntity.builder()
                .code(event.code())
                .title(event.title())
                .content(event.content())
                .memberCode(event.memberCode())
                .memberNickname(event.memberNickname())
                .updatedAt(event.updatedAt())
                .build();
    }
}
