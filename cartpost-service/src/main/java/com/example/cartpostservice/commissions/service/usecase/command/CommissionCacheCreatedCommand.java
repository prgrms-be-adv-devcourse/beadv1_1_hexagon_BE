package com.example.cartpostservice.commissions.service.usecase.command;

import com.example.cartpostservice.commissions.controller.external.dto.request.CommissionCreateRequest;

public record CommissionCacheCreatedCommand(
        String commissionCode,
        Integer plannedHires,
        Integer eligibleApplicants
) {

    public static CommissionCacheCreatedCommand from(String commissionCode, CommissionCreateRequest request) {
        return new CommissionCacheCreatedCommand(
                commissionCode,
                request.plannedHires(),
                request.eligibleApplicants()
        );
    }
}
