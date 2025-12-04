package com.example.cartpostservice.commissions.controller.dto.response.internal;

public record PeopleInfoResponseDto(
        String commissionCode,
        int applyCapacity,
        int appliedCount,
        int selectionCapacity,
        int selectedCount
) {

}
