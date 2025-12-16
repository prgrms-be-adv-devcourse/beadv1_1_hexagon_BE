package com.example.cartpostservice.commissions.service.mapper;

import com.example.cartpostservice.commissions.controller.external.dto.response.CommissionElementReadResponse;
import com.example.cartpostservice.commissions.service.usecase.result.CommissionAndTagReadResult;
import com.example.cartpostservice.commissions.service.usecase.result.CommissionElementResult;
import com.example.cartpostservice.commissions.service.usecase.result.RecruitsInfoResult;
import org.springframework.stereotype.Component;

@Component
public class CommissionResponseAssembler {

    public CommissionElementReadResponse toReadResponse(CommissionElementResult commissionElementResult) {
        CommissionAndTagReadResult commissionAndTagReadResult = commissionElementResult.commissionAndTagReadResult();
        RecruitsInfoResult recruitsInfoResult = commissionElementResult.recruitsInfoResult();

        final Integer plannedHires;
        final Integer selectedCount;
        final Integer eligibleApplicants;
        final Integer appliedCount;

        if (recruitsInfoResult != null) {
            plannedHires = recruitsInfoResult.plannedHires();
            selectedCount = recruitsInfoResult.selectedCount();
            eligibleApplicants = recruitsInfoResult.eligibleApplicants();
            appliedCount = recruitsInfoResult.appliedCount();
        } else {
            plannedHires = commissionAndTagReadResult.plannedHires();
            selectedCount = commissionAndTagReadResult.selectedCount();
            eligibleApplicants = commissionAndTagReadResult.eligibleApplicants();
            appliedCount = commissionAndTagReadResult.appliedCount();
        }

        return new CommissionElementReadResponse(
                commissionAndTagReadResult.title(),
                commissionAndTagReadResult.content(),
                commissionAndTagReadResult.paymentType(),
                commissionAndTagReadResult.unitAmount(),
                commissionAndTagReadResult.startedAt(),
                commissionAndTagReadResult.endedAt(),
                commissionAndTagReadResult.recruitmentStatus(),
                commissionAndTagReadResult.writerName(),
                commissionAndTagReadResult.tagCodes(),
                plannedHires,
                selectedCount,
                eligibleApplicants,
                appliedCount
        );

    }
}
