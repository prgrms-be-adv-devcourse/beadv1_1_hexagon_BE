package com.example.searchservice.commission.service.dto;

import com.example.searchservice.common.vo.PaymentType;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record CartPostCommissionDto(
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

}
