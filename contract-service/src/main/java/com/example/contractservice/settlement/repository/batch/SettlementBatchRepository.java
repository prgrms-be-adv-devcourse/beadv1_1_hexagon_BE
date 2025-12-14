package com.example.contractservice.settlement.repository.batch;

import com.example.contractservice.settlement.domain.Settlement;
import com.example.contractservice.settlement.repository.SettlementJdbcRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SettlementBatchRepository {

    private final SettlementJdbcRepository settlementJdbcRepository;

    public void updateAllInBatch(List<Settlement> settlements) {
        settlementJdbcRepository.updateAllInBatch(settlements);
    }
}
