package com.example.searchservice.selfpromotion.dto;

import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;
import java.time.Instant;

public record SelfPromotionDto(
        String code,
        String title,
        String content,
        String memberNickname,
        Instant updatedAt
) {
    public static SelfPromotionDto from(SelfPromotionDocumentEntity selfPromotionDocumentEntity) {
        return new SelfPromotionDto(
                selfPromotionDocumentEntity.getCode(),
                selfPromotionDocumentEntity.getTitle(),
                selfPromotionDocumentEntity.getContent(),
                selfPromotionDocumentEntity.getMemberNickname(),
                selfPromotionDocumentEntity.getUpdatedAt()
        );
    }
}
