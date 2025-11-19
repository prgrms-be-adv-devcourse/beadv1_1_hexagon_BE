package com.example.paymentservice.payment.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

public record DepositRechargeRequest(
        @Schema(description = "예치금을 충전하는 멤버 코드", example = "8172516b-2076-460f-805d-e60cbc1463l1")
        String memberCode,
        @Schema(description = "추가할 금액", example = "30000")
        @Min(value = 0L, message = "충전 금액은 음수일 수 없습니다.")
        Long amount
) {

}