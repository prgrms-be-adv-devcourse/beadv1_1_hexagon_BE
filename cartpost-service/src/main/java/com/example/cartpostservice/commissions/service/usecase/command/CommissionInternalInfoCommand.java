package com.example.cartpostservice.commissions.service.usecase.command;

import com.example.cartpostservice.commissions.controller.external.dto.request.CommissionCreateRequest;
import java.util.List;

public record CommissionInternalInfoCommand(
        String commissionCode,
        List<String> fileKeys,
        Integer plannedHires,
        Integer eligibleApplicants
) {

    public static CommissionInternalInfoCommand from(String commissionCode, CommissionCreateRequest request) {
        return new CommissionInternalInfoCommand(
                commissionCode,
                request.fileKeys(),
                request.plannedHires(),
                request.eligibleApplicants()
        );
    }
}
