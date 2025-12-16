package com.example.cartpostservice.commissions.service.event;

import com.example.cartpostservice.commissions.model.vo.RecruitmentStatus;
import com.example.cartpostservice.commissions.service.usecase.command.CommissionAndTagPartitionInfoCommand;
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

}
