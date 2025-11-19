package com.example.searchservice.selfpromotion.service.dto;

import com.example.searchservice.common.vo.PaymentType;
import java.time.Instant;

public record ProfileSelfPromotionDto(
        String code,
        String title,
        String content,
        String memberCode,
        String memberNickname,
        PaymentType paymentType,
        Long payAmount,
        Instant updatedAt
) {

}
