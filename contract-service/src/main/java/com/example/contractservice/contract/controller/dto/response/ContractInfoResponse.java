package com.example.contractservice.contract.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ContractInfoResponse(
    @Schema(description = "계약 코드", example = "4cd54740-91dc-4bcd-856c-1b776fc227b6")
    String code,
    @Schema(description = "계약 상태",
        allowableValues = {"REQUESTED", "PAID", "IN_PROGRESS", "DONE"},
        example = "IN_PROGRESS")
    String status
) {

    public static ContractInfoResponse of(String code, String status) {
        return new ContractInfoResponse(code, status);
    }
}
