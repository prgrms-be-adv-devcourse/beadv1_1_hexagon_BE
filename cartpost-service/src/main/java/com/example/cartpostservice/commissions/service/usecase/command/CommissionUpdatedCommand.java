package com.example.cartpostservice.commissions.service.usecase.command;

import com.example.cartpostservice.commissions.controller.external.dto.request.CommissionUpdateRequest;
import java.time.LocalDate;
import java.util.List;
import org.hexagon.core.vo.PaymentType;

public record CommissionUpdatedCommand(
        String commissionCode,

        String title,

        String content,

        PaymentType paymentType,

        Long unitAmount,

        LocalDate startedAt,

        LocalDate endedAt,

        List<String> tagCodes,

        Integer plannedHires,

        Integer eligibleApplicants,

        List<String> fileKeys

) {

    public static CommissionUpdatedCommand from(String commissionCode, CommissionUpdateRequest request) {
        return new CommissionUpdatedCommand(
                commissionCode,
                request.title(),
                request.content(),
                request.paymentType(),
                request.unitAmount(),
                request.startedAt(),
                request.endedAt(),
                request.tagCode(),
                request.plannedHires(),
                request.eligibleApplicants(),
                request.fileKeys()
        );
    }
}
