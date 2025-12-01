package com.example.contractservice.contract.domain.vo;

import com.example.contractservice.contract.common.ContractStatus;
import com.example.contractservice.common.PaymentType;
import java.time.Instant;

public record ContractInfo(
    String clientCode,
    String freelancerCode,
    String commissionCode,
    Instant startedAt,
    Instant endedAt,
    PaymentType paymentType,
    Long unitAmount,
    ContractStatus status
) {

    public ContractInfo pay() {
        return new ContractInfo(clientCode, freelancerCode, commissionCode, startedAt, endedAt, paymentType,
                unitAmount, ContractStatus.PAID);
    }
}
