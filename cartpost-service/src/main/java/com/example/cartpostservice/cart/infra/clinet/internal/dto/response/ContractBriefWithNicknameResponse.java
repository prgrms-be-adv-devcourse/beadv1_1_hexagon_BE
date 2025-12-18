package com.example.cartpostservice.cart.infra.clinet.internal.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public record ContractBriefWithNicknameResponse(
        @Schema(description = "계약 코드", example = "8172516b-2076-460f-805d-a60cbc0463a9")
        String code,
        @Schema(description = "클라이언트 닉네임", example = "홍길동")
        String clientName,
        @Schema(description = "프리랜서 닉네임", example = "JohnDoe")
        String freelancerName,
        @Schema(description = "프로젝트 시작일", example = "2024-05-15T12:34:56.789Z")
        Instant startedAt,
        @Schema(description = "프로젝트 종료일", example = "2024-07-15T12:34:56.789Z")
        Instant endedAt,
        @Schema(description = "결제 타입", example = "PER_JOB", allowableValues = {"PER_JOB", "MONTHLY"})
        String paymentType,
        @Schema(description = "단위 금액", example = "10000")
        Long unitAmount,
        @Schema(description = "계약명", example = "홍길동과 JohnDoe의 계약")
        String name
) {

}
