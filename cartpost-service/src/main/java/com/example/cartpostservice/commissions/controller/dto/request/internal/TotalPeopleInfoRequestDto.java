package com.example.cartpostservice.commissions.controller.dto.request.internal;

public record TotalPeopleInfoRequestDto(
        String commissionCode,
        int applyCapacity,
        int selectedCapacity
) {

}
