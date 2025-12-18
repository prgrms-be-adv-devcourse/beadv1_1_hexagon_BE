package com.example.cartpostservice.commissions.controller.external.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.hexagon.core.vo.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "커미션 생성 요청 DTO")
public record CommissionCreateRequest(

        @Schema(description = "의뢰 제목", example = "디자인 작업 요청")
        String title,

        @Schema(description = "의뢰 내용 상세", example = "상세 설명을 여기에 입력하세요.")
        String content,

        @Schema(description = "결제 방식", example = "PER_JOB")
        PaymentType paymentType,

        @Schema(description = "단위 금액", example = "50000")
        Long unitAmount,

        @Schema(description = "시작 날짜 (yyyy-MM-dd)", example = "2025-01-01")
        LocalDate startedAt,

        @Schema(description = "종료 날짜 (yyyy-MM-dd)", example = "2025-01-31")
        LocalDate endedAt,

        @Schema(description = "태그 코드 리스트", example = "[\"tag-uuid-1\", \"tag-uuid-2\"]")
        List<String> tagCodes,

        @Schema(description = "고용 예정 인원 수", example = "10")
        Integer plannedHires,

        @Schema(description = "지원 가능 인원 수", example = "50")
        Integer eligibleApplicants,

        @Schema(description = "사용자가 업로드한 파일들")
        List<String> fileKeys
) {

}
