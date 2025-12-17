package com.example.profileservice.common.model.vo.util;

import java.time.Instant;
import org.hexagon.core.vo.PaymentType;

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

    public ContractInfo cancel() {
        return new ContractInfo(clientCode, freelancerCode, commissionCode, startedAt, endedAt, paymentType,
                unitAmount, ContractStatus.CANCELLED);
    }

    public ContractInfo done() {
        return new ContractInfo(clientCode, freelancerCode, commissionCode, startedAt, endedAt, paymentType,
                unitAmount, ContractStatus.DONE);
    }

    public ContractInfo progress() {
        return new ContractInfo(clientCode, freelancerCode, commissionCode, startedAt, endedAt, paymentType,
                unitAmount, ContractStatus.IN_PROGRESS);
    }
}
