package com.example.contractservice.settlement.repository;

import static com.example.contractservice.settlement.service.mapper.SettlementMapper.toDomain;

import com.example.contractservice.settlement.common.SettlementStatus;
import com.example.contractservice.settlement.domain.Settlement;
import com.example.contractservice.settlement.service.mapper.SettlementMapper;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SettlementBatchRepository {

    private final SettlementJdbcRepository settlementJdbcRepository;

    public Settlement findById(Long lastSettlementId) {
        return toDomain(settlementJdbcRepository.findById(lastSettlementId));
    }

    public List<Settlement> findAllBatchOrderWithoutCursor(SettlementStatus status, Instant endProgressingAt,
            int limit) {
        return settlementJdbcRepository.findAllBatchOrderWithoutCursor(status.name(), endProgressingAt, limit)
                .stream()
                .map(SettlementMapper::toDomain)
                .toList();
    } // 실제로 사용되지 않는 커스텀 Reader용

    public List<Settlement> findAllBatchOrderWithCursor(SettlementStatus status, long cursorId,
            Instant cursorProgressingAt, Instant endProgressingAt, int limit) {
        return settlementJdbcRepository.findAllBatchOrderWithCursor(status.name(), cursorId, cursorProgressingAt,
                        endProgressingAt, limit)
                .stream()
                .map(SettlementMapper::toDomain)
                .toList();
    } // 실제로 사용되지 않는, 커스텀 Reader용

    public void updateAllInBatch(List<Settlement> settlements) {
        settlementJdbcRepository.updateAllInBatch(settlements);
    }
}
