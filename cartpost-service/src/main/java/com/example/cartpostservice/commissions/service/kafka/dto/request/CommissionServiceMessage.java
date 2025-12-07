package com.example.cartpostservice.commissions.service.kafka.dto.request;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.hexagon.core.vo.PaymentType;

public record CommissionServiceMessage(
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
