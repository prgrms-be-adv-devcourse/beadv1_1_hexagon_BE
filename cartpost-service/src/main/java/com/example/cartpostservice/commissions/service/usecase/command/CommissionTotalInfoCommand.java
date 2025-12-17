package com.example.cartpostservice.commissions.service.usecase.command;

import com.example.cartpostservice.commissions.controller.external.dto.request.CommissionCreateRequest;
import java.time.LocalDate;
import java.util.List;
import org.hexagon.core.vo.PaymentType;

public record CommissionTotalInfoCommand(
        String memberCode,
        String nickName,
        String title,
        String content,
        PaymentType paymentType,
        Long unitAmount,
        LocalDate startedAt,
        LocalDate endedAt,
        List<String> tagCodes,
        Integer plannedHires,
        Integer eligibleApplicants
) {

    public static CommissionTotalInfoCommand from(String memberCode, String nickName, CommissionCreateRequest request) {
        return new CommissionTotalInfoCommand(
                memberCode,
                nickName,
                request.title(),
                request.content(),
                request.paymentType(),
                request.unitAmount(),
                request.startedAt(),
                request.endedAt(),
                request.tagCodes(),
                request.plannedHires(),
                request.eligibleApplicants()
        );
    }
}
