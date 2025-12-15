package com.example.contractservice.deposit.controller.dto.response;

import com.example.contractservice.deposit.domain.Deposit;
import io.swagger.v3.oas.annotations.media.Schema;

public record DepositInfoResponse (
    @Schema(description = "현재 예치금 금액", example = "0") Long amount
){
    public static DepositInfoResponse of(Deposit deposit) {
        return new DepositInfoResponse(deposit.getAmount());
    }
}
