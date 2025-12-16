package com.example.cartpostservice.commissions.service.usecase.command;

import org.hexagon.core.vo.PaymentType;
import java.time.LocalDate;

public record CommissionsServiceCommand(
        String memberCode,

        String title,

        String content,

        PaymentType paymentType,

        Long unitAmount,

        LocalDate startedAt,

        LocalDate endedAt,

        String writerName
) {

    public static CommissionsServiceCommand from(CommissionTotalInfoCommand command) {
        return new CommissionsServiceCommand(
                command.memberCode(),
                command.title(),
                command.content(),
                command.paymentType(),
                command.unitAmount(),
                command.startedAt(),
                command.endedAt(),
                command.nickName()
        );
    }

}
