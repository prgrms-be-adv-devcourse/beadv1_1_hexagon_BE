package com.example.cartpostservice.commissions.service.usecase.command;

import com.example.cartpostservice.commissions.model.vo.RecruitmentStatus;
import com.example.cartpostservice.commissions.service.usecase.result.CommissionAndTagReadResult;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.hexagon.core.vo.PaymentType;

public record CommissionAndTagPartitionInfoCommand(
        String commissionCode,
        String memberCode,
        String writerName,
        String title,
        String content,
        PaymentType paymentType,
        Long unitAmount,
        RecruitmentStatus recruitmentStatus,
        LocalDate startedAt,
        LocalDate endedAt,
        List<String> tagCodes,
        Instant updatedAt
) {

    public static CommissionAndTagPartitionInfoCommand from(
            CommissionAndTagReadResult result) {
        return new CommissionAndTagPartitionInfoCommand(
                result.commissionCode(),
                result.memberCode(),
                result.writerName(),
                result.title(),
                result.content(),
                result.paymentType(),
                result.unitAmount(),
                result.recruitmentStatus(),
                result.startedAt(),
                result.endedAt(),
                result.tagCodes(),
                result.updatedAt()
        );
    }
}
