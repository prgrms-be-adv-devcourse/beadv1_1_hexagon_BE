package com.example.profileservice.selfPromotion.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import org.hexagon.core.vo.PaymentType;

public record SelfPromotionResponse(

        @Schema(description = "프로모션 고유 식별 코드", example = "a1b2c3d4-e5f6-a7b8-c9d0-e1f2a3b4c5d6")
        String promotionCode,

        @Schema(description = "회원 코드 (작성자)", example = "member-uuid-code")
        String memberCode,

        @Schema(description = "작성자 닉네임", example = "프리랜서_")
        String memberNickname,

        @Schema(description = "프로모션 제목", example = "Spring Cloud 기반 MSA 전문가를 찾으세요?")
        String title,

        @Schema(description = "프로모션 내용 (어필)", example = "복잡한 분산 환경 시스템 구축 경험을 보유했습니다.")
        String content,

        @Schema(description = "지급 방식", example = "MONTHLY")
        PaymentType paymentType,

        @Schema(description = "단위 금액", example = "5000000")
        Long unitAmount,

        @Schema(description = "연결된 이력서 코드", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", nullable = true)
        String resumeCode,

        @Schema(description = "생성 일시", example = "2025-11-10T03:00:00Z")
        Instant createdAt,

        @Schema(description = "수정 일시", example = "2025-11-10T03:00:00Z")
        Instant updatedAt
) {

}
