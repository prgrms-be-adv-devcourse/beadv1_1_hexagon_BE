package com.example.contractservice.settlement.repository;

import com.example.contractservice.settlement.common.SettlementStatus;
import com.example.contractservice.settlement.domain.Settlement;
import com.example.contractservice.settlement.domain.exception.SettlementErrorCode;
import com.example.contractservice.settlement.domain.exception.SettlementException;
import com.example.contractservice.settlement.entity.SettlementEntity;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SettlementJdbcRepository {

    private static final int EXPECTED_UPDATED_NUM = 1;
    private final JdbcTemplate jdbcTemplate;

    public SettlementEntity findById(Long lastSettlementId) {
        String sql = """
                SELECT *
                FROM settlements
                WHERE id = ?
                """;

        log.info("findById: {}", lastSettlementId);
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, settlementRowMapper(), lastSettlementId))
                .orElseThrow(() -> new SettlementException(SettlementErrorCode.SETTLEMENT_NOT_EXISTS));
    }

    public List<SettlementEntity> findAllBatchOrderWithoutCursor(String status, Instant endProgressingAt, int limit) {
        String sql = """
                SELECT *
                FROM settlements
                WHERE status = ? AND progressing_at < ?
                ORDER BY status, progressing_at
                LIMIT ?
                """;

        return jdbcTemplate.query(sql, ps -> {
            ps.setString(1, status);
            ps.setString(2, endProgressingAt.toString());
            ps.setInt(3, limit);
        }, settlementRowMapper());
    }

    public List<SettlementEntity> findAllBatchOrderWithCursor(String status, long cursorId, Instant cursorProgressingAt,
            Instant endProgressingAt, int limit) {
        String sql = """
                SELECT *
                FROM settlements
                WHERE status = ? AND ((progressing_at > ? OR (progressing_at = ? AND id > ?)) AND progressing_at < ?)
                ORDER BY status, progressing_at
                LIMIT ?
                """;

        return jdbcTemplate.query(sql, ps -> {
            ps.setString(1, status);
            ps.setTimestamp(2, Timestamp.from(cursorProgressingAt));
            ps.setTimestamp(3, Timestamp.from(cursorProgressingAt));
            ps.setLong(4, cursorId);
            ps.setTimestamp(5, Timestamp.from(endProgressingAt));
            ps.setInt(6, limit);
        }, settlementRowMapper());
    }

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

    private RowMapper<SettlementEntity> settlementRowMapper() {
        return (rs, rowNum) -> SettlementEntity.builder()
                .id(rs.getLong("id"))
                .code(rs.getString("code"))
                .receiverCode(rs.getString("receiver_code"))
                .contractCode(rs.getString("contract_code"))
                .originalAmount(rs.getLong("original_amount"))
                .status(SettlementStatus.valueOf(rs.getString("status")))
                .progressingAt(rs.getTimestamp("progressing_at").toInstant())
                .createdAt(rs.getTimestamp("created_at").toInstant())
                .build();
    }
}
