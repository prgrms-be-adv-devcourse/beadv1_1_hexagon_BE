package com.example.contractservice.contract.controller.dto.response;

import com.example.contractservice.contract.entity.CommissionsCapacity;

public record CommissionCapacityResponse(
        int applyCapacity,
        int appliedCount,
        int selectionCapacity,
        int selectedCapacity
) {
    public static CommissionCapacityResponse from(CommissionsCapacity commissionsCapacity) {
        return new CommissionCapacityResponse(
                commissionsCapacity.getApplyCapacity(),
                commissionsCapacity.getAppliedCount(),
                commissionsCapacity.getSelectionCapacity(),
                commissionsCapacity.getSelectedCount()
        );
    }
}
