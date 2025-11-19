package com.example.searchservice.selfpromotion.service.mapper;

import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;
import com.example.searchservice.selfpromotion.service.dto.ProfileSelfPromotionDto;

public class SelfPromotionMapper {

    public static SelfPromotionDocumentEntity toSelfPromotionDocument(ProfileSelfPromotionDto profileSelfPromotionDto) {
        return SelfPromotionDocumentEntity.builder()
                .code(profileSelfPromotionDto.code())
                .title(profileSelfPromotionDto.title())
                .content(profileSelfPromotionDto.content())
                .memberNickname(profileSelfPromotionDto.memberNickname())
                .paymentType(profileSelfPromotionDto.paymentType())
                .payAmount(profileSelfPromotionDto.payAmount())
                .updatedAt(profileSelfPromotionDto.updatedAt())
                .build();
    }
}
