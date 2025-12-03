package com.example.contractservice.settlement.service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.contractservice.common.TestConfig;
import com.example.contractservice.deposit.entity.DepositEntity;
import com.example.contractservice.deposit.repository.DepositJpaRepository;
import com.example.contractservice.settlement.common.SettlementStatus;
import com.example.contractservice.settlement.domain.Settlement;
import com.example.contractservice.settlement.entity.SettlementEntity;
import com.example.contractservice.settlement.repository.SettlementJpaRepository;
import com.example.contractservice.settlement.service.mapper.SettlementMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@Import(TestConfig.class)
class SettlementServiceTest {

    @Autowired
    SettlementService settlementService;
    @Autowired
    SettlementJpaRepository settlementJpaRepository;
    @Autowired
    DepositJpaRepository depositJpaRepository;

    @Value("${admin.member.code}")
    String adminMemberCode;
    @Value("${batch.settlement.settlement-rate}")
    BigDecimal settlementRate;

    @MockitoBean
    KafkaTemplate<String, String> kafkaTemplate;
    @MockitoBean
    KafkaAdmin kafkaAdmin;

    @AfterEach
    void tearDown() {
        depositJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("정산을 성공적으로 수행할 수 있다.")
    void success_process_settlement_given_normal() {
        // given
        long originalAmount = 10000L;
        String memberCode = "memberCode";

        DepositEntity initAdminDeposit = DepositEntity.createBy(adminMemberCode);
        initAdminDeposit.updateInfo(originalAmount);
        depositJpaRepository.save(initAdminDeposit);

        depositJpaRepository.save(DepositEntity.createBy(memberCode));

        SettlementEntity settlementEntity = SettlementEntity.builder()
                .code(UUID.randomUUID().toString())
                .receiverCode(memberCode)
                .progressingAt(Instant.now().minus(1L, ChronoUnit.DAYS))
                .contractCode(UUID.randomUUID().toString())
                .originalAmount(originalAmount)
                .createdAt(Instant.now())
                .status(SettlementStatus.BEFORE)
                .build();
        Settlement settlement = SettlementMapper.toDomain(settlementJpaRepository.save(settlementEntity));

        // when
        settlementService.processSettlement(settlement);

        // then
        long allFee = settlementRate.multiply(BigDecimal.valueOf(originalAmount)).longValue();

        DepositEntity adminDeposit = depositJpaRepository.findByMemberCode(adminMemberCode).get();
        DepositEntity memberDeposit = depositJpaRepository.findByMemberCode(memberCode).get();

        assertEquals(allFee, adminDeposit.getAmount());
        assertEquals(originalAmount - allFee, memberDeposit.getAmount());
    }

    @Test
    @DisplayName("같은 대상 예치금에 정산을 동시에 처리할 수 있다.")
    @Disabled("현재는 정산을 동시에 처리하는 로직이 없으며, 어노테이션 로직의 문제로 동시에 처리될 수 없는 상황이라 비활성화")
    void success_process_settlement_given_concurrent_request() throws Exception {
        // given
        String memberCode = "memberCode";
        int limit = 3;
        long originalAmount = 10000L;

        DepositEntity initAdminDeposit = DepositEntity.createBy(adminMemberCode);
        initAdminDeposit.updateInfo(originalAmount * limit);
        depositJpaRepository.save(initAdminDeposit);

        DepositEntity memberDepositEntity = DepositEntity.createBy(memberCode);
        depositJpaRepository.save(memberDepositEntity); // 정산자

        List<SettlementEntity> settlementEntities = IntStream.range(0, limit).mapToObj(i -> {
            SettlementEntity settlementEntity = SettlementEntity.builder()
                    .code(UUID.randomUUID().toString())
                    .receiverCode(memberCode)
                    .progressingAt(Instant.now().minus(1L, ChronoUnit.DAYS))
                    .contractCode(UUID.randomUUID().toString())
                    .originalAmount(originalAmount)
                    .createdAt(Instant.now())
                    .status(SettlementStatus.BEFORE)
                    .build();
            return settlementJpaRepository.save(settlementEntity);
        }).toList();

        List<Settlement> settlements = settlementEntities.stream()
                .map(SettlementMapper::toDomain)
                .toList();

        ExecutorService threadPool = Executors.newFixedThreadPool(limit);
        CountDownLatch countDownLatch = new CountDownLatch(limit);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(limit);

        // when
        settlements.forEach(settlement -> {
            try {
                threadPool.execute(() -> {
                    try {
                        cyclicBarrier.await();
                        settlementService.processSettlement(settlement);
                        countDownLatch.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                        countDownLatch.countDown();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();

        // then
        long allFee = settlementRate.multiply(BigDecimal.valueOf(originalAmount)).longValue() * limit;

        DepositEntity adminDeposit = depositJpaRepository.findByMemberCode(adminMemberCode).get();
        DepositEntity memberDeposit = depositJpaRepository.findByMemberCode(memberCode).get();

        assertEquals(allFee, adminDeposit.getAmount());
        assertEquals(originalAmount * limit - allFee, memberDeposit.getAmount());
    }
}
