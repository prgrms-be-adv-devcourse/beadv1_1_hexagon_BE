package com.example.contractservice.deposit.service.dto.response;

public record DepositCreatedResponse(
        String code // deposit code
) {

    public static DepositCreatedResponse of(String code) {
        return new DepositCreatedResponse(code);
    }
}
