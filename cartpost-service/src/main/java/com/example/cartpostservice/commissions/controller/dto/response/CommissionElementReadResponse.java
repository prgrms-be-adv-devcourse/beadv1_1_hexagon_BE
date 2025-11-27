package com.example.cartpostservice.commissions.controller.dto.response;

import java.time.LocalDate;
import java.util.List;
import org.hexagon.core.vo.PaymentType;

public record CommissionElementReadResponse(

        String title,

        String content,

        PaymentType paymentType,

        String unitAmount,

        LocalDate startedAt,

        LocalDate endedAt,

        boolean isOpen,

        String writerName,

        List<String> tagCode
) {

}
