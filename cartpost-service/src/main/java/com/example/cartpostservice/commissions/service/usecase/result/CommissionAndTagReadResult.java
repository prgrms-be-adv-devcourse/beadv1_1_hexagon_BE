package com.example.cartpostservice.commissions.service.usecase.result;

import com.example.cartpostservice.commissions.model.vo.RecruitmentStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.hexagon.core.vo.PaymentType;

public record CommissionAndTagReadResult(
        String commissionCode,

        String memberCode,

        String title,

        String content,

        PaymentType paymentType,

        Long unitAmount,

        LocalDate startedAt,

        LocalDate endedAt,

        RecruitmentStatus recruitmentStatus,

        String writerName,

        Instant updatedAt,

        Integer plannedHires,

        Integer selectedCount,

        Integer eligibleApplicants,

        Integer appliedCount,

        Instant lastSyncTime,

        List<String> tagCodes

) {

    public static CommissionAndTagReadResult from(CommissionReadResult commissionReadResult,
            TagsReadResult tagsReadResult) {
        return new CommissionAndTagReadResult(
                commissionReadResult.code(),
                commissionReadResult.memberCode(),
                commissionReadResult.title(),
                commissionReadResult.content(),
                commissionReadResult.paymentType(),
                commissionReadResult.unitAmount(),
                commissionReadResult.startedAt(),
                commissionReadResult.endedAt(),
                commissionReadResult.recruitmentStatus(),
                commissionReadResult.writerName(),
                commissionReadResult.updatedAt(),
                commissionReadResult.plannedHires(),
                commissionReadResult.selectedCount(),
                commissionReadResult.eligibleApplicants(),
                commissionReadResult.appliedCount(),
                commissionReadResult.lastSyncTime(),
                tagsReadResult.tagCodes()
        );
    }
}
