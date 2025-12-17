package com.example.contractservice.settlement.service.batch.reader;

import static java.time.ZoneOffset.UTC;

import com.example.contractservice.settlement.common.SettlementStatus;
import com.example.contractservice.settlement.entity.SettlementEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class SettlementZeroOffsetItemReader extends JpaPagingItemReader<SettlementEntity> {

    private static final long MONTH_INTERVAL = 1L;

    @PersistenceContext
    private EntityManager entityManager;

    private Map<String, Object> hintValues;
    private Map<String, Object> paramMap;

    public SettlementZeroOffsetItemReader(EntityManagerFactory emFactory,
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
        ORDER BY s.status, s.progressingAt
        """); // 인덱스 (status, processing_at)

        setPageSize(fetchSize);
        this.hintValues = Map.of("org.hibernate.fetchSize", fetchSize); // 하이버네이트에서 DB 레코드를 한 번에 가져오는 사이즈

        Instant curInstant = Instant.parse(dateStr);
        LocalDate endLocalDate = curInstant.atZone(UTC).toLocalDate(); // Instant -> LocalDate(년-월-일)

        Instant endInstant = endLocalDate.atStartOfDay(UTC).toInstant(); // LocalDate(년-월-일) 자정 -> Instant
        Instant startInstant = endLocalDate.minusMonths(MONTH_INTERVAL) // 한 달 전 LocalDate
                .atStartOfDay().toInstant(UTC); // 한 달 전 자정 -> Instant


        paramMap = Map.of("start", startInstant, "end", endInstant, "status", SettlementStatus.BEFORE);
    }

    @Override
    protected void doReadPage() {

        Query query = entityManager.createQuery("""
        SELECT
            s
        FROM
            SettlementEntity s
        WHERE
            s.progressingAt >= :start AND s.progressingAt < :end AND s.status = :status
        ORDER BY s.status, s.progressingAt
        """).setFirstResult(0).setMaxResults(getPageSize());

        if (paramMap != null) {
            for (Map.Entry<String, Object> me : paramMap.entrySet()) {
                query.setParameter(me.getKey(), me.getValue());
            }
        }

        if (this.hintValues != null) {
            this.hintValues.forEach(query::setHint);
        }

        if (results == null) {
            results = new CopyOnWriteArrayList<>();
        }
        else {
            results.clear();
        }

        results.addAll(query.getResultList());
    }
}

