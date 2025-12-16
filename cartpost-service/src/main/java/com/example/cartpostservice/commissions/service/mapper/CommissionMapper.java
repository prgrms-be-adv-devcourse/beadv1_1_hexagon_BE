package com.example.cartpostservice.commissions.service.mapper;

import com.example.cartpostservice.commissions.model.CommissionsEntity;
import com.example.cartpostservice.commissions.model.vo.RecruitmentStatus;
import com.example.cartpostservice.commissions.service.usecase.command.CommissionsServiceCommand;
import com.example.cartpostservice.commissions.service.usecase.result.CommissionReadResult;
import org.springframework.stereotype.Component;

@Component
public class CommissionMapper {

    public CommissionsEntity toEntity(CommissionsServiceCommand command) {
        return CommissionsEntity.builder()
                .memberCode(command.memberCode())
                .title(command.title())
                .content(command.content())
                .paymentType(command.paymentType())
                .unitAmount(command.unitAmount())
                .startedAt(command.startedAt())
                .endedAt(command.endedAt())
                .writerName(command.writerName())
                .recruitmentStatus(RecruitmentStatus.OPEN)
                .build();
    }

    public CommissionReadResult toDto(CommissionsEntity commission) {
        return new CommissionReadResult(
                commission.getCode(),
                commission.getMemberCode(),
                commission.getTitle(),
                commission.getContent(),
                commission.getPaymentType(),
                commission.getUnitAmount(),
                commission.getStartedAt(),
                commission.getEndedAt(),
                commission.getRecruitmentStatus(),
                commission.getWriterName(),
                commission.getUpdatedAt(),
                commission.getCacheApplyCapacity(),
                commission.getCacheAppliedCount(),
                commission.getCacheSelectionCapacity(),
                commission.getCacheSelectedCount(),
                commission.getLastSyncTime()
        );
    }

    ;
}
