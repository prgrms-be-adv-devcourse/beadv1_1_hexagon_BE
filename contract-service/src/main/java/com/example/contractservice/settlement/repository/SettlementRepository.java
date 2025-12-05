package com.example.contractservice.settlement.repository;

import com.example.contractservice.settlement.entity.SettlementEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SettlementRepository {
    private final SettlementJpaRepository settlementJpaRepository;

    public SettlementEntity save(SettlementEntity settlementEntity) {
        return settlementJpaRepository.save(settlementEntity);
    }

    public void saveAll(List<SettlementEntity> settlementEntities) {
        settlementJpaRepository.saveAll(settlementEntities);
    }

    public void hardDeleteAllBy(String contractCode) {
        settlementJpaRepository.deleteByContractCode(contractCode);
    }
}
