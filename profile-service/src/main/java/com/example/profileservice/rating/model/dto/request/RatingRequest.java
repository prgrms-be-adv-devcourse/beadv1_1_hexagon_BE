package com.example.profileservice.rating.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RatingRequest(
        @Schema(description = "평가 대상 계약 코드", example = "co-12345678-abcd-efgh-ijkl")
        @NotBlank(message = "계약 코드는 필수입니다.")
        String contractCode,

        @Schema(description = "만족 여부 (true: 만족, false: 불만족)", example = "true")
        @NotNull(message = "만족 여부는 필수입니다.")
        Boolean satisfied
) {

}
