package com.example.cartpostservice.commissions.service.usecase.result;

import com.example.cartpostservice.commissions.model.vo.RecruitmentStatus;
import java.time.Instant;
import java.time.LocalDate;
import org.hexagon.core.vo.PaymentType;

public record CommissionIndexReadResult(
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
        Instant updatedAt) {

}
