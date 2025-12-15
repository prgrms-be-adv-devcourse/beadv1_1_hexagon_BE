package com.example.contractservice.contract.controller.dto.response;

import com.example.contractservice.contract.domain.Contract;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

// TODO: 제네릭 페이지 DTO로 통합
public record ContractListWithCursorResponse(
        @Schema(description = "계약 목록")
        List<ContractBriefResponse> contracts,
        @Schema(description = "마지막 커서 정보")
        Instant cursorDate,
        @Schema(description = "마지막 커서 계약 코드", example = "4cd54740-91dc-4bcd-856c-1b776fc227b6")
        String cursorCode,
        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext
) {

    public static ContractListWithCursorResponse of(List<Contract> contractEntities, int pageSize) {
        List<ContractBriefResponse> briefResponses = contractEntities.stream()
                .map(ContractBriefResponse::from)
                .toList();

        if (briefResponses.size() <= pageSize) { // 마지막 지점
            return new ContractListWithCursorResponse(
                    briefResponses,
                    null,
                    null,
                    false
            );
        }

        ContractBriefResponse lastData = briefResponses.get(pageSize - 1);

        return new ContractListWithCursorResponse(
                briefResponses.subList(0, pageSize), // 페이지만큼 자름
                lastData.createdAt(),
                lastData.contractCode(),
                true
        );
    }
}
