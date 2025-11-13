package com.example.searchservice.selfpromotion.mapper;

import com.example.searchservice.selfpromotion.dto.SelfPromotionDto;
import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;

public class SelfPromotionMapper {

    public static SelfPromotionDocumentEntity toSelfPromotionDocument(SelfPromotionDto selfPromotionDto) {
        return SelfPromotionDocumentEntity.builder()
                .code(selfPromotionDto.code())
                .title(selfPromotionDto.title())
                .content(selfPromotionDto.content())
                .memberNickname(selfPromotionDto.memberNickname())
                .updatedAt(selfPromotionDto.updatedAt())
                .build();
    }
}
