package com.example.profileservice.selfPromotion.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import org.hexagon.core.vo.PaymentType;

public record SelfPromotionUpdateRequest(

        @Schema(description = "프로모션 제목", example = "Spring Cloud 기반 MSA 전문가를 찾으세요? (업데이트)")
        @Size(max = 255)
        String title,

        @Schema(description = "프로모션 내용", example = "최신 기술 스택으로 업데이트 했습니다.")
        String content,

        @Schema(description = "지급 방식 (MONTHLY: 월급, PER_JOB: 건당)", example = "PER_JOB")
        PaymentType paymentType,

        @Schema(description = "단위 금액", example = "500000")
        Long unitAmount,

        @Schema(description = "연결할 이력서 코드 (선택 사항)", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", nullable = true)
        String resumeCode,

        @Schema(description = "PDF 파일의 S3 Key (선택 사항)", example = "self_promotions/uuid-file.pdf", nullable = true)
        String pdfKey
) {

}
