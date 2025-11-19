package com.example.cartpostservice.commissions.controller.dto.request;

import org.hexagon.core.vo.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "커미션 생성 요청 DTO")
public record CommissionUpsertRequest(

        @Schema(description = "의뢰 제목", example = "디자인 작업 요청")
        @NotBlank(message = "title은 반드시 입력해야 합니다.")
        String title,

        @Schema(description = "의뢰 내용 상세", example = "상세 설명을 여기에 입력하세요.")
        @NotNull(message = "content는 반드시 입력해야 합니다.")
        String content,

        @Schema(description = "결제 방식", example = "PER_JOB")
        @NotNull(message = "paymentType은 반드시 선택해야 합니다.")
        PaymentType paymentType,

        @Schema(description = "단위 금액", example = "50000")
        @NotBlank(message = "unitAmount는 반드시 입력해야 합니다.")
        String unitAmount,

        @Schema(description = "시작 날짜 (yyyy-MM-dd)", example = "2025-01-01")
        @NotNull(message = "startedAt은 반드시 입력해야 합니다.")
        LocalDate startedAt,

        @Schema(description = "종료 날짜 (yyyy-MM-dd)", example = "2025-01-31")
        @NotNull(message = "endedAt은 반드시 입력해야 합니다.")
        LocalDate endedAt,

        @Schema(description = "태그 코드 리스트", example = "[\"tag-uuid-1\", \"tag-uuid-2\"]")
        @NotNull(message = "tagCode 리스트는 반드시 입력해야 합니다.")
        List<String> tagCode

) {

}
