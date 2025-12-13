package com.example.contractservice.deposit.controller.dto.response;

import com.example.contractservice.deposit.domain.DepositHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.time.Instant;

public record DepositHistoryCursorResponse(
        @Schema(description = "예치금 내역 정보")
        List<DepositHistoryInfo> infos,
        @Schema(description = "마지막 커서 정보")
        Instant CursorDate,
        @Schema(description = "마지막 커서 예치금 내역 코드", example = "4df54740-91dc-4bcd-856c-1b776fc227b6")
        String cursorCode,
        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext
) {
    public static DepositHistoryCursorResponse of(List<DepositHistory> histories, int pageSize) {
        List<DepositHistoryInfo> infos = histories.stream()
                .map(DepositHistoryInfo::from)
                .toList();

        if (infos.size() <= pageSize) {
            return new DepositHistoryCursorResponse(
                    infos,
                    null,
                    null,
                    false
            );
        }

        return new DepositHistoryCursorResponse(
                infos.subList(0, pageSize),
                histories.get(pageSize - 1).getCreatedAt(),
                histories.get(pageSize - 1).getCode(),
                true
        );
    }
}
