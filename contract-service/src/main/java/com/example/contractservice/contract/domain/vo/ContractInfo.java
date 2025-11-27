package com.example.contractservice.contract.domain.vo;

import com.example.contractservice.contract.common.ContractStatus;
import java.time.Instant;
import org.hexagon.core.vo.PaymentType;

public record ContractInfo(
    String requestorCode,
    String contractorCode,
    String freelancerCode,
    Instant startedAt,
    Instant endedAt,
    PaymentType paymentType,
    Long unitAmount,
    ContractStatus status
) {

    public ContractInfo confirm() {
        return new ContractInfo(requestorCode, contractorCode, freelancerCode, startedAt, endedAt, paymentType,
                unitAmount, ContractStatus.CONFIRMED);
    }

    public ContractInfo pay() {
        return new ContractInfo(requestorCode, contractorCode, freelancerCode, startedAt, endedAt, paymentType,
                unitAmount, ContractStatus.PAID);
    }
}
