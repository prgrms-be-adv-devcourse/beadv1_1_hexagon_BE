package com.example.cartpostservice.commissions.controller.dto.response;

import org.hexagon.core.vo.PaymentType;
import java.time.LocalDate;
import java.util.List;

public record CommissionElementReadResponse(

        String title,

        String content,

        PaymentType paymentType,

        String unitAmount,

        LocalDate startedAt,

        LocalDate endedAt,

        boolean isOpen,

        String writerName,

        List<String> tagCode,

        int plannedHires,

        int selectedCount,

        int eligibleApplicants,

        int appliedCount
) {

}
