package com.example.contractservice.settlement.service.dto.request;

import java.time.Instant;
import org.hexagon.core.vo.PaymentType;

public record SettlementSaveRequest(
        String receiverCode,
        String contractCode,
        Long amount,
        Instant startedAt,
        Instant endedAt,
        PaymentType paymentType
) {

}
