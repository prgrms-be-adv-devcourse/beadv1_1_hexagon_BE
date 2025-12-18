package com.example.contractservice.contract.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.contractservice.common.TestConfig;
import com.example.contractservice.common.util.feign.CommissionClient;
import com.example.contractservice.common.util.feign.MemberClient;
import com.example.contractservice.contract.common.ContractStatus;
import com.example.contractservice.contract.controller.dto.request.ContractCancelRequest;
import com.example.contractservice.contract.controller.dto.request.ContractCreateRequest;
import com.example.contractservice.contract.controller.dto.response.ContractPayResponse;
import com.example.contractservice.contract.domain.Contract;
import com.example.contractservice.contract.entity.CommissionsCapacity;
import com.example.contractservice.contract.entity.ContractEntity;
import com.example.contractservice.contract.repository.CommissionsCapacityJpaRepository;
import com.example.contractservice.contract.repository.ContractJpaRepository;
import com.example.contractservice.contract.service.dto.request.ContractPayServiceRequest;
import com.example.contractservice.contract.service.dto.response.CommissionRecruitmentResponse;
import com.example.contractservice.contract.service.dto.response.MemberInfoResponse;
import com.example.contractservice.contract.service.dto.response.MemberInfoResponse.MemberInfo;
import com.example.contractservice.contract.service.dto.response.MemberInfoResponse.MemberRole;
import com.example.contractservice.contract.service.mapper.ContractMapper;
import com.example.contractservice.deposit.entity.DepositEntity;
import com.example.contractservice.deposit.repository.DepositHistoryJpaRepository;
import com.example.contractservice.deposit.repository.DepositJpaRepository;
import com.example.contractservice.settlement.entity.SettlementEntity;
import com.example.contractservice.settlement.repository.SettlementJpaRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import org.hexagon.core.dto.ResponseDto;
import org.hexagon.core.vo.PaymentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@Import(TestConfig.class)
class ContractServiceTest {
    @Autowired
    SettlementJpaRepository settlementJpaRepository;
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
    @MockitoBean
    MemberClient memberClient;
    @MockitoBean
    CommissionClient commissionClient;

    @Value("${admin.member.code}")
    String adminMemberCode;
    @Autowired
    private DepositHistoryJpaRepository depositHistoryJpaRepository;

    @BeforeEach
    void setUp() {
        depositJpaRepository.save(DepositEntity.createBy(adminMemberCode));
    }

    @AfterEach
    void tearDown() {
        contractJpaRepository.deleteAllInBatch();
        depositJpaRepository.deleteAllInBatch();
        settlementJpaRepository.deleteAllInBatch();
        depositHistoryJpaRepository.deleteAllInBatch();
        commissionsCapacityJpaRepository.deleteAllInBatch();
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

    @Test
    @DisplayName("PAID인 계약을 성공적으로 취소(환불)할 수 있다")
    void success_cancel_contract_given_normal_paid_contract() {
        // given
        String clientCode = UUID.randomUUID().toString();
        String freelancerCode = UUID.randomUUID().toString();

        long unitAmount = 500_000L;
        int contractsNum = 2;

        DepositEntity clientDepositEntity = DepositEntity.createBy(clientCode);
        clientDepositEntity.updateInfo(unitAmount * contractsNum);
        depositJpaRepository.save(clientDepositEntity);

        List<Contract> contracts = IntStream.range(0, contractsNum)
                .mapToObj(i ->
                        contractJpaRepository.save(ContractEntity.builder()
                                .name("name" + i)
                                .unitAmount(unitAmount)
                                .code(UUID.randomUUID().toString())
                                .commissionCode(UUID.randomUUID().toString())
                                .startedAt(Instant.now().plus(1, ChronoUnit.DAYS))
                                .endedAt(Instant.now().plus(3, ChronoUnit.DAYS))
                                .status(ContractStatus.REQUESTED)
                                .clientCode(clientCode)
                                .freelancerCode(freelancerCode)
                                .body("body" + i)
                                .paymentType(PaymentType.PER_JOB)
                                .build()))
                .map(ContractMapper::toDomain)
                .toList(); // 결제 전 계약 생성

        contracts.stream()
                .map(contract -> contract.getInfo().commissionCode())
                .forEach(commissionCode -> commissionsCapacityJpaRepository.save(
                        CommissionsCapacity.createBy(commissionCode, 5, 3))); // 의뢰글 수용 인원 생성

        contractService.payContracts(new ContractPayServiceRequest(clientCode, contracts.stream().map(Contract::getCode).toList())); // 계약 결제 처리

        // when
        List<ContractCancelRequest> requests = List.of(
                new ContractCancelRequest(clientCode, contracts.get(0).getCode()), // 클라이언트가 취소하는 경우
                new ContractCancelRequest(freelancerCode, contracts.get(1).getCode()) // 프리랜서가 취소하는 경우
        );

        requests.forEach(contractService::cancelContract);

        // then
        DepositEntity adminDeposit = depositJpaRepository.findByMemberCode(adminMemberCode).get();
        DepositEntity clientDeposit = depositJpaRepository.findByMemberCode(clientCode).get();
        List<SettlementEntity> allSettlements = settlementJpaRepository.findAll();

        assertEquals(0L, adminDeposit.getAmount());
        assertEquals(unitAmount * contractsNum, clientDeposit.getAmount());
        assertEquals(0, allSettlements.size());
    }

    @Test
    @DisplayName("의뢰글에 동시 지원 요청이 들어와도 지원 인원 이상으로 요청이 수행될 수 없다")
    void success_blocking_over_request_on_full_commission() throws Exception {
        // given
        String clientCode = UUID.randomUUID().toString();
        String freelancerCode = UUID.randomUUID().toString();
        String commissionCode = UUID.randomUUID().toString();

        int applyCapacity = 5; // 최대 지원 인원
        int selectionCapacity = 3;

        commissionsCapacityJpaRepository.save(
                CommissionsCapacity.createBy(commissionCode, applyCapacity, selectionCapacity));

        int tryCount = applyCapacity * 2;
        ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        CountDownLatch countDownLatch = new CountDownLatch(tryCount);

        List<MemberInfo> memberInfoList = List.of(new MemberInfo(clientCode, "클라이언트", MemberRole.CLIENT),
                new MemberInfo(freelancerCode, "프리랜서", MemberRole.FREELANCER));

        when(memberClient.getMemberInfo(any()))
                .thenReturn(new ResponseDto<>(0, HttpStatus.OK.value(), "", new MemberInfoResponse(memberInfoList)));
        when(commissionClient.getRecruitmentStatus(commissionCode))
                .thenReturn(new ResponseDto<>(0, HttpStatus.OK.value(), "", new CommissionRecruitmentResponse(true)));

        // when
        IntStream.range(0, tryCount)
                .forEach(i -> threadPool.execute(() -> {
                            try {
                                contractService.requestContract(
                                        new ContractCreateRequest(clientCode,
                                                freelancerCode,
                                                commissionCode,
                                                Instant.now(),
                                                Instant.now(),
                                                "PER_JOB",
                                                5000L,
                                                "name" + i,
                                                "body" + i));
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                            finally {
                                countDownLatch.countDown();
                            }
                        })
                );

        countDownLatch.await();

        // then
        CommissionsCapacity commissionsCapacity = commissionsCapacityJpaRepository.findByCommissionCode(commissionCode).get();
        assertEquals(applyCapacity, commissionsCapacity.getAppliedCount());
        verify(memberClient, times(applyCapacity)).getMemberInfo(any());
        verify(commissionClient, times(applyCapacity)).getRecruitmentStatus(commissionCode);
    }
}
