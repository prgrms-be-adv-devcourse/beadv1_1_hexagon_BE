package com.example.contractservice.settlement.repository;

import com.example.contractservice.settlement.domain.Settlement;
import com.example.contractservice.settlement.domain.exception.SettlementErrorCode;
import com.example.contractservice.settlement.domain.exception.SettlementException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SettlementJdbcRepository {

    private static final int EXPECTED_UPDATED_NUM = 1;
    private final JdbcTemplate jdbcTemplate;

    public void updateAllInBatch(List<Settlement> settlements) {
        String sql = """
                UPDATE settlements
                SET settlement_rate = ?, settled_amount = ?, settled_at = ?, status = ?
                WHERE id = ?
                """;

        List<Object[]> args = settlements.stream().map(settlement -> new Object[]{
                settlement.getSettlementStatusInfo().settlementRate(),
                settlement.getSettlementStatusInfo().settledAmount(),
                settlement.getSettlementTimeline().settledAt(),
                settlement.getSettlementStatusInfo().status().name(),
                settlement.getId()
        }).toList();

        int[] updatedCounts = jdbcTemplate.batchUpdate(sql, args);
        boolean allUpdated = Arrays.stream(updatedCounts).allMatch(i -> i == EXPECTED_UPDATED_NUM);

        if (!allUpdated) {
            throw new SettlementException(SettlementErrorCode.SETTLEMENT_BATCH_UPDATE_FAILED);
        }
    }
}
