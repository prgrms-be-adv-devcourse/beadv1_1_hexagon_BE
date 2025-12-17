package com.example.searchservice.selfpromotion.dto;

import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;
import org.hexagon.core.vo.PaymentType;

public record SelfPromotionResponseDto(
        String code,
        String title,
        String content,
        String memberCode,
        String memberNickname,
        PaymentType paymentType,
        Long payAmount
) {
    public static SelfPromotionResponseDto from(SelfPromotionDocumentEntity selfPromotionDocumentEntity) {
        return new SelfPromotionResponseDto(
                selfPromotionDocumentEntity.getCode(),
                selfPromotionDocumentEntity.getTitle(),
                selfPromotionDocumentEntity.getContent(),
                selfPromotionDocumentEntity.getMemberCode(),
                selfPromotionDocumentEntity.getMemberNickname(),
                selfPromotionDocumentEntity.getPaymentType(),
                selfPromotionDocumentEntity.getPayAmount()
        );
    }
}
