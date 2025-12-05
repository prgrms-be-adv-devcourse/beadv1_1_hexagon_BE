package com.example.contractservice.settlement.service.batch.processor;

import com.example.contractservice.settlement.domain.Settlement;
import com.example.contractservice.settlement.entity.SettlementEntity;
import com.example.contractservice.settlement.service.SettlementService;
import com.example.contractservice.settlement.service.mapper.SettlementMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
public class SettlementDataProcessor implements ItemProcessor<SettlementEntity, Settlement> {
    private final SettlementService settlementService;

    @Override
    public Settlement process(SettlementEntity settlementEntity) {
        Settlement settlement = SettlementMapper.toDomain(settlementEntity);

        settlementService.processSettlement(settlement);

        return settlement;
    }
}
