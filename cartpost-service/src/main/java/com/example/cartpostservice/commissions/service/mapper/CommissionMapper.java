package com.example.cartpostservice.commissions.service.mapper;

import com.example.cartpostservice.commissions.model.CommissionsEntity;
import com.example.cartpostservice.commissions.model.vo.RecruitmentStatus;
import com.example.cartpostservice.commissions.service.usecase.command.CommissionsServiceCommand;

public class CommissionMapper {

    public static CommissionsEntity toEntity(CommissionsServiceCommand command) {
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
}
