package com.example.cartpostservice.commissions.controller.internal.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RecruitmentStatusRequest(
        @Schema(description = "조회하고 싶은 의뢰글 코드를 전달해 주세요")
        @NotBlank(message = "의뢰글 uuid를 전달해 주시기 바랍니다")
        String commissionCode
) {

}
