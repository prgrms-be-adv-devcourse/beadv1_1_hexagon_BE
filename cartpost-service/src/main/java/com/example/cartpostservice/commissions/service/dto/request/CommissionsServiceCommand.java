package com.example.cartpostservice.commissions.service.dto.request;

import java.time.LocalDate;
import org.hexagon.core.vo.PaymentType;

public record CommissionsServiceCommand(
        String memberCode,

        String title,

        String content,

        PaymentType paymentType,

        String unitAmount,

        LocalDate startedAt,

        LocalDate endedAt,

        String writerName
) {

}
