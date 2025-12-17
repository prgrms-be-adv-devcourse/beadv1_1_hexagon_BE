package com.example.cartpostservice.commissions.infra.client.internal.dto.response;

public record PeopleInfoResponseDto(
        String commissionCode,
        int applyCapacity,
        int appliedCount,
        int selectionCapacity,
        int selectedCount
) {

}
