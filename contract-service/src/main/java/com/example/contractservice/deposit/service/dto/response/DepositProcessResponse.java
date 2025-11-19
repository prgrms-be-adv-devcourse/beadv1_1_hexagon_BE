package com.example.contractservice.deposit.service.dto.response;

public record DepositProcessResponse(
        String code, // 예치금 코드
        String memberCode, // 회원 코드
        Long amount // 총 금액
) {

}
