package com.example.cartpostservice.commissions.service.dto.response;

import java.time.Instant;
import org.hexagon.core.vo.PaymentType;
import java.time.LocalDate;

public record CommissionsServiceResult(
        String code,

        String memberCode,

        String title,

        String content,

        PaymentType paymentType,

        String unitAmount,

        LocalDate startedAt,

        LocalDate endedAt,

        boolean isOpen,

        String writerName,

        Instant updatedAt
) {

}
