package com.example.contractservice.contract.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.contractservice.common.TestConfig;
import com.example.contractservice.contract.common.ContractStatus;
import com.example.contractservice.contract.controller.dto.response.ContractPayResponse;
import com.example.contractservice.contract.entity.CommissionsCapacity;
import com.example.contractservice.contract.entity.ContractEntity;
import com.example.contractservice.contract.repository.CommissionsCapacityJpaRepository;
import com.example.contractservice.contract.repository.ContractJpaRepository;
import com.example.contractservice.contract.service.dto.request.ContractPayServiceRequest;
import com.example.contractservice.deposit.entity.DepositEntity;
import com.example.contractservice.deposit.repository.DepositJpaRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hexagon.core.vo.PaymentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
class ContractServiceTest {
    @Autowired
    DepositJpaRepository depositJpaRepository;
    @Autowired
    ContractJpaRepository contractJpaRepository;
    @Autowired
    ContractService contractService;
    @Autowired
    CommissionsCapacityJpaRepository commissionsCapacityJpaRepository;
    @MockitoBean
    KafkaTemplate<String, String> kafkaTemplate;
    @MockitoBean
    KafkaAdmin kafkaAdmin;

    @Value("${admin.member.code}")
    String adminMemberCode;

    @BeforeEach
    void setUp() {
        depositJpaRepository.save(DepositEntity.createBy(adminMemberCode));
    }

    @AfterEach
    void tearDown() {
        contractJpaRepository.deleteAllInBatch();
        depositJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("잘못된 계약 요청은 결제에 실패하고 제대로 된 계약 요청은 결제에 성공한다")
    void success_pay_contracts_given_normal_and_fail_abnormal() {
        // given
        String userCode = UUID.randomUUID().toString();
        DepositEntity userDeposit = DepositEntity.createBy(userCode);
        userDeposit.updateInfo((long) Integer.MAX_VALUE);
        depositJpaRepository.save(userDeposit);

        List<String> contractCodes = new ArrayList<>();
        int entireTestNum = 5;
        int testLimit = 4;
        int normalLimit = 3;
        long unitAmount = 500_000L;

        for (int i = 0; i < testLimit; i++) {
            String commissionCode = UUID.randomUUID().toString();
            ContractEntity contractEntity = ContractEntity.builder()
                    .name("name" + i)
                    .code(UUID.randomUUID().toString())
                    .clientCode(userCode)
                    .commissionCode(commissionCode)
                    .freelancerCode(UUID.randomUUID().toString())
                    .paymentType(PaymentType.PER_JOB)
                    .unitAmount(unitAmount)
                    .status(i < normalLimit ? ContractStatus.REQUESTED : ContractStatus.IN_PROGRESS)
                    .startedAt(Instant.now().plus(1, ChronoUnit.DAYS))
                    .endedAt(Instant.now().plus(3, ChronoUnit.DAYS))
                    .body("body" + i)
                    .build();
            contractCodes.add(contractJpaRepository.save(contractEntity).getCode());
            commissionsCapacityJpaRepository.save(CommissionsCapacity.createBy(commissionCode, 5, 3));
        } // 정상 3, 비정상 1

        ContractEntity contractEntity = ContractEntity.builder()
                .name("name" + testLimit)
                .code(UUID.randomUUID().toString())
                .clientCode(UUID.randomUUID().toString())
                .commissionCode(UUID.randomUUID().toString())
                .freelancerCode(UUID.randomUUID().toString())
                .paymentType(PaymentType.PER_JOB)
                .unitAmount(unitAmount)
                .status(ContractStatus.REQUESTED)
                .startedAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .endedAt(Instant.now().plus(3, ChronoUnit.DAYS))
                .body("body" + testLimit)
                .build(); // 비정상 1
        contractCodes.add(contractJpaRepository.save(contractEntity).getCode());

        ContractPayResponse contractPayResponse = contractService.payContracts(
                new ContractPayServiceRequest(userCode, contractCodes));

        // then - 2개 실패 필요
        DepositEntity adminDeposit = depositJpaRepository.findByMemberCode(adminMemberCode).get();

        assertEquals(normalLimit, contractPayResponse.success().size());
        assertEquals(entireTestNum - normalLimit, contractPayResponse.fail().size());
        assertEquals(unitAmount * normalLimit, adminDeposit.getAmount());
    }

}
