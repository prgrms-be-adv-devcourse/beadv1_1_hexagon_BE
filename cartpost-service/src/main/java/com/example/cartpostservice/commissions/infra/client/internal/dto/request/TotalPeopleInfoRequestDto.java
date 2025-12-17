package com.example.cartpostservice.commissions.infra.client.internal.dto.request;

public record TotalPeopleInfoRequestDto(
        String commissionCode,
        Integer applyCapacity,
        Integer selectionCapacity
) {

}
