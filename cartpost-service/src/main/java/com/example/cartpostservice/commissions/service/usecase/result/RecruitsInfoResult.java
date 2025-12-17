package com.example.cartpostservice.commissions.service.usecase.result;

import com.example.cartpostservice.commissions.infra.client.internal.dto.response.PeopleInfoResponseDto;
import java.time.Instant;

public record RecruitsInfoResult(
        int plannedHires,

        int selectedCount,

        int eligibleApplicants,

        int appliedCount,

        Instant syncTime
) {

    public static RecruitsInfoResult from(PeopleInfoResponseDto peopleInfoResponseDto) {
        return new RecruitsInfoResult(
                peopleInfoResponseDto.selectionCapacity(),
                peopleInfoResponseDto.selectedCount(),
                peopleInfoResponseDto.applyCapacity(),
                peopleInfoResponseDto.appliedCount(),
                Instant.now()
        );
    }
}
