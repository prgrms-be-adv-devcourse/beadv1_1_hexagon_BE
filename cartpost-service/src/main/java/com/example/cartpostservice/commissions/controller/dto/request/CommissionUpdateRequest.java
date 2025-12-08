package com.example.cartpostservice.commissions.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import org.hexagon.core.vo.PaymentType;

public record CommissionUpdateRequest(
        @Schema(description = "의뢰 제목", example = "디자인 작업 요청")
        @Size(min = 2, max = 100, message = "제목은 2자 이상 100자 이하로 입력해주세요.")
        String title,

        @Schema(description = "의뢰 내용 상세", example = "상세 설명을 여기에 입력하세요.")
        String content,

        @Schema(description = "결제 방식", example = "PER_JOB")
        PaymentType paymentType,

        @Schema(description = "단위 금액", example = "50000")
        @Pattern(regexp = "^[0-9]+$", message = "금액은 숫자만 입력 가능합니다.")
        String unitAmount,

        @Schema(description = "시작 날짜 (yyyy-MM-dd)", example = "2025-01-01")
        @FutureOrPresent(message = "시작 날짜는 현재 혹은 미래여야 합니다.")
        LocalDate startedAt,

        @Schema(description = "종료 날짜 (yyyy-MM-dd)", example = "2025-01-31")
        @Future(message = "종료 날짜는 미래여야 합니다.")
        LocalDate endedAt,

        @Schema(description = "태그 코드 리스트", example = "[\"tag-uuid-1\", \"tag-uuid-2\"]")
        List<String> tagCode,

        @Schema(description = "고용 예정 인원 수", example = "10")
        @Positive(message = "고용 예정 인원은 1명 이상이어야 합니다.")
        Integer plannedHires,

        @Schema(description = "지원 가능 인원 수", example = "50")
        @Positive(message = "지원 가능 인원은 1명 이상이어야 합니다.")
        Integer eligibleApplicants,

        @Schema(description = "사용자가 업로드한 파일들")
        List<String> fileKeys
) {

}
