package com.example.searchservice.saga.mapper;

import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;
import org.hexagon.core.events.selfpromotion.SelfPromotionCreatedEvent;
import org.hexagon.core.events.selfpromotion.SelfPromotionUpdatedEvent;
import org.hexagon.core.vo.PaymentType;
import org.hexagon.core.vo.SelfPromotion;

public class SelfPromotionMapper {

    public static SelfPromotionDocumentEntity toSelfPromotionDocument(SelfPromotion selfPromotion) {
        return SelfPromotionDocumentEntity.builder()
                .code(selfPromotion.code())
                .title(selfPromotion.title())
                .content(selfPromotion.content())
                .memberNickname(selfPromotion.memberNickname())
                .paymentType(PaymentType.valueOf(String.valueOf(selfPromotion.paymentType())))
                .payAmount(selfPromotion.payAmount())
                .updatedAt(selfPromotion.updatedAt())
                .build();
    }

    public static SelfPromotionDocumentEntity toSelfPromotionDocument(SelfPromotionCreatedEvent createdEvent) {
        return SelfPromotionDocumentEntity.builder()
                .code(createdEvent.code())
                .title(createdEvent.title())
                .content(createdEvent.content())
                .memberNickname(createdEvent.memberNickname())
                .paymentType(PaymentType.valueOf(String.valueOf(createdEvent.paymentType())))
                .payAmount(createdEvent.payAmount())
                .updatedAt(createdEvent.updatedAt())
                .build();
    }

    public static SelfPromotionDocumentEntity toSelfPromotionDocument(SelfPromotionUpdatedEvent updatedEvent) {
        return SelfPromotionDocumentEntity.builder()
                .code(updatedEvent.code())
                .title(updatedEvent.title())
                .content(updatedEvent.content())
                .memberNickname(updatedEvent.memberNickname())
                .paymentType(PaymentType.valueOf(String.valueOf(updatedEvent.paymentType())))
                .payAmount(updatedEvent.payAmount())
                .updatedAt(updatedEvent.updatedAt())
                .build();
    }
}
