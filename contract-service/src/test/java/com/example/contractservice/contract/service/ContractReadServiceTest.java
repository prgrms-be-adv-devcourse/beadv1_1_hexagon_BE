package com.example.contractservice.contract.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.example.contractservice.contract.common.ContractStatus;
import com.example.contractservice.contract.common.Order;
import com.example.contractservice.contract.controller.dto.response.ContractBriefResponse;
import com.example.contractservice.contract.controller.dto.response.ContractDetailResponse;
import com.example.contractservice.contract.controller.dto.response.ContractListWithCursorResponse;
import com.example.contractservice.contract.entity.ContractEntity;
import com.example.contractservice.contract.repository.ContractJpaRepository;
import com.example.contractservice.contract.service.dto.request.ContractDetailRequest;
import com.example.contractservice.contract.service.dto.request.ContractReadCursorRequest;
import com.example.contractservice.contract.service.dto.response.MemberInfoResponse;
import com.example.contractservice.contract.service.dto.response.MemberInfoResponse.MemberInfo;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.hexagon.core.vo.PaymentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class ContractReadServiceTest {

    private static final int PAGE_SIZE = 20;
    @Autowired
    private ContractJpaRepository contractJpaRepository;
    @Autowired
    private ContractReadService contractReadService;

    @MockBean
    RestTemplate restTemplate;

    private String memberCode;

    @BeforeEach
    void setUp() {
        Random random = new Random(System.currentTimeMillis());
        memberCode = UUID.randomUUID().toString();

        for (int i = 0; i < 40; i++) {
            String opponentCode = UUID.randomUUID().toString();
            int zeroOrOne = random.nextInt(2);

            ContractEntity entity = ContractEntity.builder()
                    .code(UUID.randomUUID().toString())
                    .requestorCode(zeroOrOne % 2 == 0 ? memberCode : opponentCode)
                    .contractorCode(zeroOrOne % 2 == 0 ? opponentCode : memberCode)
                    .freelancerCode(opponentCode)
                    .name("이름" + i)
                    .body("내용" + i)
                    .status(ContractStatus.values()[random.nextInt(ContractStatus.values().length)]) // 상태 랜덤 선택
                    .startedAt(Instant.now())
                    .endedAt(Instant.now())
                    .unitAmount(random.nextLong(20000000L))
                    .paymentType(PaymentType.MONTHLY)
                    .build();

            contractJpaRepository.save(entity);
        } // 더미 데이터 40개
    }

    @AfterEach
    void tearDown() {
        contractJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("사용자는 20개씩 자신의 계약을 내림차순으로 확인할 수 있다.")
    void success_find_all_contracts_given_normal_args() {
        // given - setUp()에서 수행
        // when
        ContractListWithCursorResponse firstResp = contractReadService.findAllBy(
                new ContractReadCursorRequest(memberCode, null, null, Order.DESC));// 최초 조회
        ContractListWithCursorResponse secondResp = contractReadService.findAllBy(
                new ContractReadCursorRequest(memberCode, firstResp.cursorDate(), firstResp.cursorCode(),
                        Order.DESC));// 두 번째 조회

        // then
        List<ContractBriefResponse> firstContracts = firstResp.contracts();
        List<ContractBriefResponse> secondContracts = secondResp.contracts();

        assertEquals(PAGE_SIZE, firstContracts.size());
        assertEquals(PAGE_SIZE, secondContracts.size());
        assertTrue(firstResp.hasNext());
        assertFalse(secondResp.hasNext());

        assertOrderDesc(firstContracts);
        assertOrderDesc(secondContracts);
    }

    @Test
    @DisplayName("계약 상세 내용을 조회할 수 있다.")
    void success_find_detail_given_normal_code() {
        // given
        String body = "내용~";
        String name = "이름";
        ContractStatus done = ContractStatus.DONE;
        String opponentCode = UUID.randomUUID().toString();

        ContractEntity entity = ContractEntity.builder()
                .code(UUID.randomUUID().toString())
                .requestorCode(memberCode)
                .contractorCode(opponentCode)
                .freelancerCode(memberCode)
                .name(name)
                .body(body)
                .status(done)
                .startedAt(Instant.now())
                .endedAt(Instant.now())
                .unitAmount(20000000L)
                .paymentType(PaymentType.MONTHLY)
                .build();

        ContractEntity saved = contractJpaRepository.save(entity);

        when(restTemplate.getForObject(any(), eq(MemberInfoResponse.class)))
                .thenReturn(new MemberInfoResponse(List.of(
                        new MemberInfo(memberCode, "멤버 닉네임", true),
                        new MemberInfo(opponentCode, "상대방 닉네임", false)))
                );

        // when
        ContractDetailResponse detailResponse = contractReadService.findDetailBy(
                new ContractDetailRequest(memberCode, saved.getCode()));

        // then
        assertEquals(body, detailResponse.body());
        assertEquals(name, detailResponse.name());
        assertEquals(done.name(), detailResponse.status());
    }

    private static void assertOrderDesc(List<ContractBriefResponse> contracts) {
        for (int i = 1; i < contracts.size(); i++) {
            Instant pre = contracts.get(i - 1).createdAt();
            Instant cur = contracts.get(i).createdAt();

            assertTrue(pre.isAfter(cur) || pre.equals(cur)); // 정렬 순서 검증
        }
    }
}
