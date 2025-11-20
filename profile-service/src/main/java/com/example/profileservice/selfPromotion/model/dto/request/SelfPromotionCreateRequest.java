package com.example.profileservice.selfPromotion.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hexagon.core.vo.PaymentType;

public record SelfPromotionCreateRequest(

        @Schema(description = "프로모션 제목", example = "Spring Cloud 기반 MSA 전문가를 찾으세요?")
        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 255)
        String title,

        @Schema(description = "프로모션 내용", example = "복잡한 분산 환경 시스템 구축 경험을 보유했습니다.")
        @NotBlank(message = "내용은 필수입니다.")
        String content,

        @Schema(description = "지급 방식 (MONTHLY: 월급, PER_JOB: 건당)", example = "MONTHLY")
        @NotNull(message = "지급 방식은 필수입니다.")
        PaymentType paymentType,

        @Schema(description = "단위 금액", example = "5000000")
        @NotNull(message = "단위 금액은 필수입니다.")
        Long unitAmount,

        @Schema(description = "연결할 이력서 코드 (선택 사항)", example = "sa546a6-asd7f-sd57fs-sd5f7ds567ds5d", nullable = true)
        String resumeCode
) {

}
