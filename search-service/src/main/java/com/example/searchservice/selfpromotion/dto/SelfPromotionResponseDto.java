package com.example.searchservice.selfpromotion.dto;

import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;

public record SelfPromotionResponseDto(
        String code,
        String title,
        String memberCode,
        String memberNickname
) {
    public static SelfPromotionResponseDto from(SelfPromotionDocumentEntity selfPromotionDocumentEntity) {
        return new SelfPromotionResponseDto(
                selfPromotionDocumentEntity.getCode(),
                selfPromotionDocumentEntity.getTitle(),
                selfPromotionDocumentEntity.getMemberCode(),
                selfPromotionDocumentEntity.getMemberNickname()
        );
    }
}
