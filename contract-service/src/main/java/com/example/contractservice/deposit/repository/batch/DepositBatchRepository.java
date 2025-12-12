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
        // 주의: JDBC -> DB로는 Instant를 자동 계산해주지만(JVM 시간대 정보를 통해 실제로 들어갈 값으로 변환. 현재 JVM 시간대가 KST라서 9시간을 더함)
        // DB(MySQL)는 시간대 정보를 몰라서 그대로 반환하며(DB는 이게 서버 시간대인지 UTC 시간대인지 모름) 그래서 Instant.parse()로 읽어오게 되어 절대 동일 값이 될 수 없다!
        // JDBC URL에 serverTimezone을 UTC로 설정할 경우, JVM 시간대 정보를 폐기하고 JDBC가 UTC 기준으로 Instant를 조정해준다. 이것으로 헤결되었다.

        List<Object[]> args = memberDepositMap.values().stream()
                .map(deposit -> new Object[]{deposit.getAmount(), Instant.now(), deposit.getCode(), deposit.getUpdatedAt()}).toList();

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
