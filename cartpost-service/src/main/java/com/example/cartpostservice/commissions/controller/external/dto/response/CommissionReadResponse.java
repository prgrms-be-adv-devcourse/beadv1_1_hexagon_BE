package com.example.cartpostservice.commissions.controller.external.dto.response;

import org.hexagon.core.vo.PaymentType;
import java.time.LocalDate;
import java.util.List;

public record CommissionReadResponse(
        String title,

        PaymentType paymentType,

        String unitAmount,

        LocalDate startedAt,

        LocalDate endedAt,

        boolean isOpen,

        String writerName,

        List<String> tagCode
) {

}
