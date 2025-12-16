package com.example.memberservice.common.client.dto.response.rating;

import io.swagger.v3.oas.annotations.media.Schema;

public record RatingResponse(
        @Schema(description = "평가를 받은 회원의 코드", example = "sa546a6-asd7f-sd57fs-sd5f7ds567ds5d")
        String memberCode,

        @Schema(description = "받은 만족 평가 개수", example = "42")
        int satisfiedCount,

        @Schema(description = "받은 불만족 평가 개수", example = "3")
        int unsatisfiedCount
) {

}
