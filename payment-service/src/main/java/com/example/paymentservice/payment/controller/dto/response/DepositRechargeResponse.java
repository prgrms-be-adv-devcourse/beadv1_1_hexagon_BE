package com.example.paymentservice.payment.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record DepositRechargeResponse(
        @Schema(description = "충전된 예치금 코드", example = "1872516b-2076-460f-805d-e60cbc0463a9")
        String code,
        @Schema(description = "현재 총 잔액", example = "20000")
        Long amount
) {

    public static DepositRechargeResponse of(String code, Long amount) {
        return new DepositRechargeResponse(code, amount);
    }
}