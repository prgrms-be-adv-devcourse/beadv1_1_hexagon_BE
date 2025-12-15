package com.example.searchservice.saga.mapper;

import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;
import org.hexagon.core.events.selfpromotion.SelfPromotionUpsertEvent;
import org.hexagon.core.vo.SelfPromotion;

public class SelfPromotionMapper {

    public static SelfPromotionDocumentEntity toSelfPromotionDocument(SelfPromotionUpsertEvent upsertEvent) {
        return SelfPromotionDocumentEntity.builder()
                .code(upsertEvent.code())
                .title(upsertEvent.title())
                .content(upsertEvent.content())
                .memberNickname(upsertEvent.memberNickname())
                .paymentType(upsertEvent.paymentType())
                .payAmount(upsertEvent.payAmount())
                .updatedAt(upsertEvent.updatedAt())
                .build();
    }
}
