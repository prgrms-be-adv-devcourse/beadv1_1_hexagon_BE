package com.example.searchservice.saga.events.commission;

import com.example.searchservice.commission.common.PaymentType;
import com.example.searchservice.commission.entity.CommissionDocumentEntity;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record CommissionUpdatedEvent(
        String code,
        String title,
        String content,
        String memberCode,
        String memberNickname,
        List<String> tags,
        LocalDate startedAt,
        LocalDate endedAt,
        PaymentType paymentType,
        Long payAmount,
        Boolean isClosed,
        Instant updatedAt
) {
    public static CommissionDocumentEntity toCommissionDocumentEntity(CommissionUpdatedEvent event) {
        return CommissionDocumentEntity.builder()
                .code(event.code())
                .title(event.title())
                .content(event.content())
                .memberCode(event.memberCode())
                .memberNickname(event.memberNickname())
                .tags(event.tags())
                .startedAt(event.startedAt())
                .endedAt(event.endedAt())
                .paymentType(event.paymentType())
                .payAmount(event.payAmount())
                .isClosed(event.isClosed())
                .updatedAt(event.updatedAt())
                .build();
    }
}
