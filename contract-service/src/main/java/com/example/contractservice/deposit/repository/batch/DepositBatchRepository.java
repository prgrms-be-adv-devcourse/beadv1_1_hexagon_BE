package com.example.contractservice.deposit.repository.batch;

import com.example.contractservice.deposit.domain.Deposit;
import com.example.contractservice.deposit.domain.DepositHistory;
import com.example.contractservice.deposit.domain.exception.DepositErrorCode;
import com.example.contractservice.deposit.domain.exception.DepositException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DepositBatchRepository {

    private static final int EXPECTED_UPDATED_NUM = 1;

    private final JdbcTemplate jdbcTemplate;

    public void updateAllDeposits(Map<String, Deposit> memberDepositMap) { // now() 쓰면 데이터베이스 기준 시간대 시간이 들어감
        String sql = """
                UPDATE deposits
                SET amount = ?, updated_at = ?
                WHERE code = ? AND updated_at = ?
                """;

        Instant batchTime = Instant.now();

        List<Object[]> args = memberDepositMap.values().stream()
                .map(deposit -> new Object[]{deposit.getAmount(), batchTime, deposit.getCode(), deposit.getUpdatedAt()}).toList();

        int[] updatedCounts = jdbcTemplate.batchUpdate(sql, args);

        boolean allDepositUpdated = Arrays.stream(updatedCounts).allMatch(i -> i == EXPECTED_UPDATED_NUM);

        if (!allDepositUpdated) {
            throw new DepositException(DepositErrorCode.DEPOSIT_BATCH_UPDATE_FAILED);
        }
    }

    public void saveAllHistories(List<DepositHistory> depositHistories) {
        String sql = """
                INSERT INTO deposit_histories (code, is_deleted, change_amount, created_at, updated_at, result_amount, deposit_code, contract_code, summary)
                VALUES (?, 0, ?, ?, ?, ?, ?, ?, ?)
                """;

        List<Object[]> args = depositHistories.stream().map(history -> new Object[]{
                history.getCode(),
                history.getDepositChange().changeAmount(),
                history.getCreatedAt(),
                history.getCreatedAt(),
                history.getDepositChange().resultAmount(),
                history.getDepositCode(),
                history.getContractCode(),
                history.getSummary()
        }).toList();

        jdbcTemplate.batchUpdate(sql, args);
    }

}
