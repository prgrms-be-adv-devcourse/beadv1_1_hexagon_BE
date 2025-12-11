package com.example.contractservice.settlement.service.batch;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SettlementLastCursorDao {
    private final JdbcTemplate jdbcTemplate;

    public Long getLastSettlementId() {
        List<Long> queryList = jdbcTemplate.query("""
                SELECT id
                FROM settlement_last_batch_data
                """, (rs, rowNum) -> rs.getLong(1));

        if (queryList.isEmpty()) {
            return null;
        }
        return queryList.get(0);
    }

    public void saveLastSettlementId(Long lastSettlementId) {
        jdbcTemplate.update("""
                DELETE FROM settlement_last_batch_data
        """);
        jdbcTemplate.update("""
                INSERT INTO settlement_last_batch_data
                VALUES (?)
        """, lastSettlementId);
    }
}
