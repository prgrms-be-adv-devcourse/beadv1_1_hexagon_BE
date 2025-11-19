package com.example.contractservice.settlement.service.batch.processor;

import com.example.contractservice.deposit.service.DepositService;
import com.example.contractservice.deposit.service.dto.request.DepositProcessRequest;
import com.example.contractservice.settlement.domain.Settlement;
import com.example.contractservice.settlement.entity.SettlementEntity;
import com.example.contractservice.settlement.service.mapper.SettlementMapper;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
public class SettlementDataProcessor implements ItemProcessor<SettlementEntity, SettlementEntity> {
    private final DepositService depositService;

    @Value("${batch.settlement.settlement-rate}")
    private BigDecimal settlementRate;

    @Override
    public SettlementEntity process(SettlementEntity settlementEntity) {
        Settlement settlement = SettlementMapper.toDomain(settlementEntity);

        settlement.settle(settlementRate);

        DepositProcessRequest depositProcessRequest = DepositProcessRequest.from(settlement);

        depositService.transfer(depositProcessRequest);

        SettlementMapper.applyToEntity(settlement, settlementEntity);

        return settlementEntity;
    }
}
