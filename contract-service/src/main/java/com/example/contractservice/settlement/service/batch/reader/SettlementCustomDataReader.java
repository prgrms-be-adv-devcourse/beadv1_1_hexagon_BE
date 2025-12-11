package com.example.contractservice.settlement.service.batch.reader;

import static com.example.contractservice.settlement.common.SettlementStatus.*;
import static java.time.ZoneOffset.UTC;

import com.example.contractservice.settlement.domain.Settlement;
import com.example.contractservice.settlement.repository.SettlementBatchRepository;
import com.example.contractservice.settlement.service.batch.SettlementLastCursorDao;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

// 현재 사용하지 않는 클래스이며 단순 기록용입니다!

/** <div>커서 기반 페이지네이션으로 청크 사이즈만큼 데이터를 가져옵니다. <br />
 * 마지막으로 가져온 settlement의 id를 저장하고 있으며 이 값으로 인덱스를 통해 정산 청크 데이터를 가져옵니다. <br />
 * writer에서 마지막 정산 데이터의 id를 DB에 저장해야 합니다.</div>
 * <br />
 * <p>{@code doRead()} - 이전에 마지막으로 처리했던 정산 데이터를 가져옵니다.
 * 이 데이터가 null인 경우, 첫 데이터를 청크 사이즈만큼 가져오며, null이 아닌 경우, 해당 정산 데이터를 기준으로 다음 청크 사이즈의
 * 데이터를 가져옵니다. 그것을 큐에 저장합니다. </p>
 *
 */
@Slf4j
@Component
@StepScope
public class SettlementCustomDataReader extends AbstractItemCountingItemStreamItemReader<Settlement> {

    private final SettlementBatchRepository settlementBatchRepository;
    private final SettlementLastCursorDao  settlementLastCursorDao;

    private Settlement lastSettlement;
    private Instant endDate;
    private int fetchSize;
    private Queue<Settlement> queue;

    public SettlementCustomDataReader(
            SettlementLastCursorDao settlementLastCursorDao,
            @Value("#{jobParameters['dateStr']}") String dateStr,
            SettlementBatchRepository settlementBatchRepository,
            @Value("${batch.settlement.size}") int fetchSize) {
        this.settlementLastCursorDao = settlementLastCursorDao;
        this.settlementBatchRepository = settlementBatchRepository;

        log.info("dataStr: {}", dateStr);

        Instant curInstant = Instant.parse(dateStr);
        LocalDate endLocalDate = curInstant.atZone(UTC).toLocalDate(); // Instant -> LocalDate(년-월-일)
        this.endDate = endLocalDate.atStartOfDay(UTC).toInstant(); // LocalDate(년-월-일) 자정 -> Instant
        this.fetchSize = fetchSize;

        setName(ClassUtils.getShortName(SettlementCustomDataReader.class));
    }

    @Override
    protected Settlement doRead() { // 데이터를 가져올 때
        if (queue == null || queue.isEmpty()) {
            loadNextData();
        }

        return queue.poll();
    }

    private void loadNextData() {
        Long lastSettlementId = settlementLastCursorDao.getLastSettlementId();
        if (lastSettlementId != null) {
            lastSettlement = settlementBatchRepository.findById(lastSettlementId);
        }

        if (lastSettlement == null) {
            queue = new ArrayDeque<>(settlementBatchRepository.findAllBatchOrderWithoutCursor(BEFORE, endDate, fetchSize));
        } else {
            Instant lastProgressingAt = lastSettlement.getSettlementTimeline().progressingAt();
            List<Settlement> batchData = settlementBatchRepository.findAllBatchOrderWithCursor(BEFORE, lastSettlementId, lastProgressingAt, endDate, fetchSize);

            queue = new ArrayDeque<>(batchData);
        }
    }

    @Override
    protected void doOpen() { // 세션을 여는 최초 1회만 실행
    }

    @Override
    protected void doClose() { // 세션 닫을 때
    }
}
