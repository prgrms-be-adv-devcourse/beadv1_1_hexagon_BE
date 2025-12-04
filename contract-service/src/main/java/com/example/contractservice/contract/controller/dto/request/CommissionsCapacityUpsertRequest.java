package com.example.contractservice.contract.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CommissionsCapacityUpsertRequest(
        @Schema(description = "관련 의뢰글 코드", example = "a94472b1-be7d-4c5b-8342-90b5a115be9d")
        @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                message = "유효한 UUID 형식이어야 합니다.")
        String commissionCode,
        @Schema(description = "최대 지원 가능 인원 수", example = "50")
        @Size(min = 1, max = 1000, message = "최대 모집 인원 수는 1명 이상, 1000명 이하여야만 합니다.")
        Integer applyCapacity,
        @Schema(description = "최대 선정 인원 수", example = "5")
        @Size(min = 1, max = 1000, message = "최대 선정 인원 수는 1명 이상, 1000명 이하여야만 합니다.")
        Integer selectedCapacity
) {

}
