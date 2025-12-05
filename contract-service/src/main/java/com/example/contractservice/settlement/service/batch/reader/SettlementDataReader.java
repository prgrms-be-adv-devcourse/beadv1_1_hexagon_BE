package com.example.contractservice.settlement.service.batch.reader;

import static java.time.ZoneOffset.UTC;

import com.example.contractservice.settlement.common.SettlementStatus;
import com.example.contractservice.settlement.entity.SettlementEntity;
import jakarta.persistence.EntityManagerFactory;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class SettlementDataReader extends JpaCursorItemReader<SettlementEntity> { // TODO: Settlement로 변경

    private static final long MONTH_INTERVAL = 1L;

    public SettlementDataReader(EntityManagerFactory emFactory,
            @Value("#{jobParameters['dateStr']}") String dateStr,
            @Value("${batch.settlement.size}") int fetchSize) {

        setEntityManagerFactory(emFactory);

        setQueryString("""
        SELECT
            s
        FROM
            SettlementEntity s
        WHERE
            s.progressingAt >= :start AND s.progressingAt < :end AND s.status = :status
        """); // 인덱스 (status, processing_at)

        setHintValues(Map.of("org.hibernate.fetchSize", fetchSize)); // 하이버네이트에서 DB 레코드를 한 번에 가져오는 사이즈

        Instant curInstant = Instant.parse(dateStr);
        LocalDate endLocalDate = curInstant.atZone(UTC).toLocalDate(); // Instant -> LocalDate(년-월-일)

        Instant endInstant = endLocalDate.atStartOfDay(UTC).toInstant(); // LocalDate(년-월-일) 자정 -> Instant
        Instant startInstant = endLocalDate.minusMonths(MONTH_INTERVAL) // 한 달 전 LocalDate
                .atStartOfDay().toInstant(UTC); // 한 달 전 자정 -> Instant


        Map<String, Object> paramMap = Map.of("start", startInstant, "end", endInstant, "status", SettlementStatus.BEFORE.name());
        setParameterValues(paramMap);
    }
}
