package com.example.cartpostservice.commissions.service.usecase.command;

import com.example.cartpostservice.commissions.service.usecase.result.RecruitsInfoResult;
import java.time.Instant;

public record CommissionCacheUpdatedCommand(
        String commissionCode,

        int plannedHires,

        int selectedCount,

        int eligibleApplicants,

        int appliedCount,

        Instant syncTime
) {

    public static CommissionCacheUpdatedCommand from(RecruitsInfoResult recruitsInfoResult, String commissionCode) {
        return new CommissionCacheUpdatedCommand(
                commissionCode,
                recruitsInfoResult.plannedHires(),
                recruitsInfoResult.selectedCount(),
                recruitsInfoResult.eligibleApplicants(),
                recruitsInfoResult.appliedCount(),
                recruitsInfoResult.syncTime()
        );
    }
}
