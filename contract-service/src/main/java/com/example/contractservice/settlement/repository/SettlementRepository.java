package com.example.contractservice.settlement.repository;

import static com.example.contractservice.settlement.service.mapper.SettlementMapper.*;

import com.example.contractservice.settlement.domain.Settlement;
import com.example.contractservice.settlement.entity.SettlementEntity;
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
        settlements.forEach(this::save); // JDBC를 이용한 PSTMT 연결 후 한번에 처리?
    }

    public void hardDeleteAllBy(String contractCode) {
        settlementJpaRepository.deleteByContractCode(contractCode);
    }
}
