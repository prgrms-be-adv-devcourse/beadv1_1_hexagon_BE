package com.example.searchservice.commission.service.mapper;

import com.example.searchservice.commission.entity.CommissionDocumentEntity;
import com.example.searchservice.commission.service.dto.CartPostCommissionDto;

public class CommissionMapper {
    public static CommissionDocumentEntity toCommissionDocument(CartPostCommissionDto cartPostCommissionDto) {
        return CommissionDocumentEntity.builder()
                .code(cartPostCommissionDto.code())
                .title(cartPostCommissionDto.title())
                .content(cartPostCommissionDto.content())
                .memberCode(cartPostCommissionDto.memberCode())
                .memberNickname(cartPostCommissionDto.memberNickname())
                .tags(cartPostCommissionDto.tags())
                .startedAt(cartPostCommissionDto.startedAt())
                .endedAt(cartPostCommissionDto.endedAt())
                .paymentType(cartPostCommissionDto.paymentType())
                .payAmount(cartPostCommissionDto.payAmount())
                .isClosed(cartPostCommissionDto.isClosed())
                .updatedAt(cartPostCommissionDto.updatedAt())
                .build();
    }
}
