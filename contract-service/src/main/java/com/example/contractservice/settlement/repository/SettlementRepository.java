package com.example.contractservice.settlement.repository;

import static com.example.contractservice.settlement.service.mapper.SettlementMapper.*;

import com.example.contractservice.settlement.domain.Settlement;
import com.example.contractservice.settlement.entity.SettlementEntity;
import com.example.contractservice.settlement.service.mapper.SettlementMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SettlementRepository {
    private final SettlementJpaRepository settlementJpaRepository;

    public Settlement save(Settlement settlement) {
        Optional<SettlementEntity> optionalSettlement = settlementJpaRepository.findByCode(settlement.getCode());

        if (optionalSettlement.isPresent()) {
            SettlementEntity settlementEntity = optionalSettlement.get();

            applyToEntity(settlement, settlementEntity);
            return toDomain(settlementJpaRepository.save(settlementEntity));
        }

        SettlementEntity settlementEntity = toEntity(settlement);

        return toDomain(settlementJpaRepository.save(settlementEntity));
    }

    public void saveAll(List<Settlement> settlements) {
        List<SettlementEntity> settlementEntities = settlements.stream()
                .map(SettlementMapper::toEntity)
                .toList();

        settlementJpaRepository.saveAll(settlementEntities);
    }

    public void hardDeleteAllBy(String contractCode) {
        settlementJpaRepository.deleteByContractCode(contractCode);
    }
}
