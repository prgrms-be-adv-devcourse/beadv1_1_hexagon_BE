package com.example.contractservice.contract.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.contractservice.common.TestConfig;
import com.example.contractservice.contract.entity.CommissionsCapacity;
import com.example.contractservice.contract.entity.ContractEntity;
import com.example.contractservice.contract.repository.CommissionsCapacityJpaRepository;
import com.example.contractservice.contract.repository.ContractJpaRepository;
import com.example.contractservice.contract.service.ContractPayService;
import com.example.contractservice.contract.service.dto.request.ContractPayProcessRequest;
import com.example.contractservice.deposit.entity.DepositEntity;
import com.example.contractservice.deposit.repository.DepositHistoryJpaRepository;
import com.example.contractservice.deposit.repository.DepositJpaRepository;
import com.example.contractservice.settlement.common.SettlementStatus;
import com.example.contractservice.settlement.domain.Settlement;
import com.example.contractservice.settlement.entity.SettlementEntity;
import com.example.contractservice.settlement.repository.SettlementJpaRepository;
import com.example.contractservice.settlement.service.SettlementService;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.hexagon.core.vo.PaymentType;
import org.junit.jupiter.api.AfterEach;
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
class ContractPaySettlementTest {

    @Autowired
    ContractPayService contractPayService;
    @Autowired
    SettlementService settlementService;

    @Autowired
    SettlementJpaRepository settlementJpaRepository;
    @Autowired
    DepositJpaRepository depositJpaRepository;
    @Autowired
    ContractJpaRepository contractJpaRepository;
    @Autowired
    private DepositHistoryJpaRepository depositHistoryJpaRepository;

    @MockitoBean
    KafkaTemplate<String, String> kafkaTemplate;
    @MockitoBean
    KafkaAdmin kafkaAdmin;

    @Value("${admin.member.code}")
    String adminMemberCode;
    @Value("${batch.settlement.settlement-rate}")
    BigDecimal settlementRate;
    @Autowired
    private CommissionsCapacityJpaRepository commissionsCapacityJpaRepository;

    @AfterEach
    void tearDown() {
        depositJpaRepository.deleteAllInBatch();
        depositHistoryJpaRepository.deleteAllInBatch();
        contractJpaRepository.deleteAllInBatch();
        settlementJpaRepository.deleteAllInBatch();
        commissionsCapacityJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("정산으로 인한 예치금 입금, 결제로 인한 예치금 출금이 동시에 발생할 수 있다.")
    void success_process_payment_and_settlement_at_the_same_time() throws Exception{
        // given
        AtomicInteger paySuccessCount = new AtomicInteger(0);
        AtomicInteger settlementSuccessCount = new AtomicInteger(0);

        long paymentAmount = 1111L;
        long initAmount = 500_000L;
        long settlementOriginalAmount = 10000;
        String userCode = UUID.randomUUID().toString();
        DepositEntity userDeposit = DepositEntity.createBy(userCode);
        userDeposit.updateInfo(initAmount);
        DepositEntity adminDeposit = DepositEntity.createBy(adminMemberCode);
        adminDeposit.updateInfo(initAmount);

        depositJpaRepository.save(userDeposit);
        depositJpaRepository.save(adminDeposit);

        int paymentNum = 5;
        int settlementNum = 1;

        List<ContractEntity> contractEntities = IntStream.range(0, paymentNum)
                .mapToObj(i ->
                        contractJpaRepository.save(ContractEntity.builder()
                                .clientCode(userCode)
                                .freelancerCode(UUID.randomUUID().toString())
                                .code(UUID.randomUUID().toString())
                                .commissionCode(UUID.randomUUID().toString())
                                .startedAt(Instant.now().plus(1L, ChronoUnit.DAYS))
                                .endedAt(Instant.now().plus(3L, ChronoUnit.DAYS))
                                .unitAmount(paymentAmount)
                                .paymentType(PaymentType.PER_JOB)
                                .status(ContractStatus.REQUESTED)
                                .name("name" + i)
                                .body("body" + i)
                                .build())
                ).toList(); // 계약 생성
        contractEntities.forEach(contract -> commissionsCapacityJpaRepository.save(
                CommissionsCapacity.createBy(contract.getCommissionCode(), 50, 30))); // 의뢰글 수용 인원 수 생성

        List<ContractPayProcessRequest> contractPayProcessRequests = contractEntities.stream()
                .map(entity -> new ContractPayProcessRequest(
                        userCode,
                        entity.getCode())
                )
                .toList(); // 계약 결제용 DTO 생성

        List<Settlement> settlements = IntStream.range(0, settlementNum)
                .mapToObj(i -> settlementJpaRepository.save(
                        SettlementEntity.builder()
                                .contractCode(UUID.randomUUID().toString())
                                .receiverCode(userCode)
                                .originalAmount(settlementOriginalAmount)
                                .status(SettlementStatus.BEFORE)
                                .progressingAt(Instant.now().minus(1L, ChronoUnit.DAYS))
                                .createdAt(Instant.now())
                                .code(UUID.randomUUID().toString())
                                .build())
                )
                .map(SettlementMapper::toDomain)
                .toList(); // 정산용 데이터 생성

        ExecutorService threadPool = Executors.newFixedThreadPool(paymentNum + settlementNum);
        CyclicBarrier barrier = new CyclicBarrier(paymentNum + settlementNum);
        CountDownLatch countDownLatch = new CountDownLatch(paymentNum + settlementNum);

        // when
        contractPayProcessRequests.forEach(req ->
                threadPool.execute(() -> {
                    try {
                        barrier.await();

                        contractPayService.processPayment(req);

                        paySuccessCount.incrementAndGet();
                        countDownLatch.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                        countDownLatch.countDown();
                    }
                })
        );

        settlements.forEach(settlement ->
                threadPool.execute(() -> {
                    try {
                        barrier.await();

                        settlementService.processSettlement(settlement);

                        settlementSuccessCount.incrementAndGet();
                        countDownLatch.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                        countDownLatch.countDown();
                    }
                })
        );
        countDownLatch.await(9000L, TimeUnit.MILLISECONDS);

        // then
        DepositEntity afterUserDeposit = depositJpaRepository.findByMemberCode(userCode).get();
        DepositEntity afterAdminDeposit = depositJpaRepository.findByMemberCode(adminMemberCode).get();

        long settlementFee = settlementRate.multiply(BigDecimal.valueOf(settlementOriginalAmount)).longValue();
        long settledAmount = settlementOriginalAmount - settlementFee;

        System.out.println(paySuccessCount.get());
        System.out.println(settlementSuccessCount.get());

        assertEquals(initAmount - paySuccessCount.get() * paymentAmount + settlementSuccessCount.get() * settledAmount, afterUserDeposit.getAmount());
        assertEquals(initAmount + paySuccessCount.get() * paymentAmount - settlementSuccessCount.get() * settledAmount, afterAdminDeposit.getAmount());
        assertTrue(paySuccessCount.get() > 0);
    }
}
