package com.example.cartpostservice.commissions.service.dto.response;

import java.time.LocalDate;
import org.hexagon.core.vo.PaymentType;

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

        String writerName
) {

}
