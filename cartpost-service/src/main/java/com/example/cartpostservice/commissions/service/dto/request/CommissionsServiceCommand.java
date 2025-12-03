package com.example.cartpostservice.commissions.service.dto.request;

import org.hexagon.core.vo.PaymentType;;
import java.time.LocalDate;

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
