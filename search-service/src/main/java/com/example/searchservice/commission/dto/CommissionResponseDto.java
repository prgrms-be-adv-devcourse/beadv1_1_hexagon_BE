package com.example.searchservice.commission.dto;

import com.example.searchservice.commission.entity.CommissionDocumentEntity;
import java.time.LocalDate;
import java.util.List;
import org.hexagon.core.vo.PaymentType;

public record CommissionResponseDto(
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
        Boolean isOpen
) {
    public static CommissionResponseDto from(CommissionDocumentEntity commissionDocumentEntity) {
        return new CommissionResponseDto(
                commissionDocumentEntity.getCode(),
                commissionDocumentEntity.getTitle(),
                commissionDocumentEntity.getContent(),
                commissionDocumentEntity.getMemberCode(),
                commissionDocumentEntity.getMemberNickname(),
                commissionDocumentEntity.getTags(),
                commissionDocumentEntity.getStartedAt(),
                commissionDocumentEntity.getEndedAt(),
                commissionDocumentEntity.getPaymentType(),
                commissionDocumentEntity.getPayAmount(),
                commissionDocumentEntity.getIsOpen()
        );
    }
}
