package com.example.cartpostservice.commissions.service.event;

import com.example.cartpostservice.commissions.model.vo.RecruitmentStatus;
import com.example.cartpostservice.commissions.service.usecase.command.CommissionAndTagPartitionInfoCommand;
import com.example.cartpostservice.commissions.service.usecase.command.CommissionUpdatedCommand;
import com.example.cartpostservice.commissions.service.usecase.result.CommissionReadResult;
import com.example.cartpostservice.commissions.service.usecase.result.TagsReadResult;
import org.hexagon.core.events.commission.CommissionUpsertEvent;

public class CommissionUpsertEventFactory {

    public static CommissionUpsertEvent createEvent(CommissionAndTagPartitionInfoCommand partitionInfoCommand) {
        return new CommissionUpsertEvent(
                partitionInfoCommand.commissionCode(),
                partitionInfoCommand.title(),
                partitionInfoCommand.content(),
                partitionInfoCommand.memberCode(),
                partitionInfoCommand.writerName(),
                partitionInfoCommand.tagCodes(),
                partitionInfoCommand.startedAt(),
                partitionInfoCommand.endedAt(),
                partitionInfoCommand.paymentType(),
                partitionInfoCommand.unitAmount(),
                partitionInfoCommand.recruitmentStatus().equals(RecruitmentStatus.OPEN),
                partitionInfoCommand.updatedAt()
        );
    }

    public static CommissionUpsertEvent createEvent(CommissionReadResult updatedResult,
            TagsReadResult updatedTagResult) {
        return new CommissionUpsertEvent(
                updatedResult.code(),
                updatedResult.title(),
                updatedResult.content(),
                updatedResult.memberCode(),
                updatedResult.writerName(),
                updatedTagResult.tagCodes(),
                updatedResult.startedAt(),
                updatedResult.endedAt(),
                updatedResult.paymentType(),
                updatedResult.unitAmount(),
                updatedResult.recruitmentStatus().equals(RecruitmentStatus.OPEN),
                updatedResult.updatedAt()
        );
    }
}
