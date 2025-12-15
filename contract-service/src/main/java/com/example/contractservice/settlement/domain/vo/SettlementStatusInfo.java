package com.example.contractservice.settlement.domain.vo;

import static com.example.contractservice.settlement.common.SettlementStatus.*;

import com.example.contractservice.settlement.common.SettlementStatus;
import java.math.BigDecimal;

public record SettlementStatusInfo(
        Long originalAmount,
        Long settledAmount,
        SettlementStatus status,
        BigDecimal settlementRate
) {

    public SettlementStatusInfo(Long originalAmount) {
        this(originalAmount, null, BEFORE, null);
    }

    public SettlementStatusInfo settle(BigDecimal settlementRate) {
        BigDecimal preAmount = BigDecimal.valueOf(originalAmount);
        Long calculatedFeeAmount = settlementRate.multiply(preAmount).longValue(); // 곱셈 후 소숫점 이하 버림

        return new SettlementStatusInfo(originalAmount, originalAmount - calculatedFeeAmount, DONE, settlementRate);
    }
}
