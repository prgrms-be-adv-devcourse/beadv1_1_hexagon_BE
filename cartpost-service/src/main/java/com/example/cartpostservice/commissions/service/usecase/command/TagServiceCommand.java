package com.example.cartpostservice.commissions.service.usecase.command;

import java.util.List;

public record TagServiceCommand(
        String commissionsCode,

        List<String> tagCodes
) {

    public static TagServiceCommand from(String commissionCode, CommissionTotalInfoCommand commissionTotalInfoCommand) {
        return new TagServiceCommand(
                commissionCode,
                commissionTotalInfoCommand.tagCodes()
        );
    }
}
