package com.example.cartpostservice.commissions.controller.external.dto.response;

import com.example.cartpostservice.commissions.common.dto.PresignedUrlComponent;
import com.example.cartpostservice.commissions.model.vo.RecruitmentStatus;
import org.hexagon.core.vo.PaymentType;
import java.time.LocalDate;
import java.util.List;

public record CommissionElementReadResponse(

        String title,

        String content,

        PaymentType paymentType,

        Long unitAmount,

        LocalDate startedAt,

        LocalDate endedAt,

        RecruitmentStatus recruitmentStatus,

        String writerName,

        List<String> tagCode,

        int plannedHires,

        int selectedCount,

        int eligibleApplicants,

        int appliedCount
) {

}
