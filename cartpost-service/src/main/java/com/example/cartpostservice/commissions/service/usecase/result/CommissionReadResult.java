package com.example.cartpostservice.commissions.service.usecase.result;

import com.example.cartpostservice.commissions.model.vo.RecruitmentStatus;
import java.time.Instant;
import org.hexagon.core.vo.PaymentType;
import java.time.LocalDate;

public record CommissionReadResult(
        String code,

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

        int plannedHires,

        int selectedCount,

        int eligibleApplicants,

        int appliedCount,

        Instant lastSyncTime
) {

}
