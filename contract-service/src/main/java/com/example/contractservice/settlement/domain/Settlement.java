package com.example.contractservice.settlement.domain;

import static com.example.contractservice.settlement.domain.exception.SettlementErrorCode.FEE_NOT_CALCULATED;

import com.example.contractservice.settlement.domain.exception.SettlementException;
import com.example.contractservice.settlement.domain.vo.SettlementReference;
import com.example.contractservice.settlement.domain.vo.SettlementStatusInfo;
import com.example.contractservice.settlement.domain.vo.SettlementTimeline;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Settlement {

    private String code;

    private SettlementReference settlementReference;

    private SettlementStatusInfo settlementStatusInfo;

    private SettlementTimeline settlementTimeline;

    public Settlement(SettlementReference settlementReference, SettlementStatusInfo settlementStatusInfo, SettlementTimeline settlementTimeline) {
        this(null, settlementReference, settlementStatusInfo, settlementTimeline);
    }
    public Settlement(String code, SettlementReference settlementReference,
        SettlementStatusInfo settlementStatusInfo, SettlementTimeline settlementTimeline) {
        this.code = (code == null) ? generateCode() : code;
        this.settlementReference = settlementReference;
        this.settlementStatusInfo = settlementStatusInfo;
        this.settlementTimeline = settlementTimeline;
    }

    public void settle(BigDecimal settlementRate) {
        this.settlementStatusInfo = settlementStatusInfo.settle(settlementRate);
        this.settlementTimeline = settlementTimeline.updateSettledAt(Instant.now());
    }

    public String getCode() {
        return code;
    }

    public SettlementReference getSettlementReference() {
        return settlementReference;
    }

    public SettlementStatusInfo getSettlementStatusInfo() {
        return settlementStatusInfo;
    }

    public SettlementTimeline getSettlementTimeline() {
        return settlementTimeline;
    }

    private String generateCode() {
        return UUID.randomUUID().toString();
    }

    public Long getFee() {
        if (settlementStatusInfo.settledAmount() == null) {
            throw new SettlementException(FEE_NOT_CALCULATED);
        }

        return settlementStatusInfo.originalAmount() - settlementStatusInfo.settledAmount();
    }
}
