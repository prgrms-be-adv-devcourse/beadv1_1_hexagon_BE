package com.example.contractservice.contract.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.contractservice.common.TestConfig;
import com.example.contractservice.contract.common.ContractStatus;
import com.example.contractservice.contract.controller.dto.response.MemberRoleStatusResponse;
import com.example.contractservice.contract.entity.ContractEntity;
import com.example.contractservice.contract.repository.ContractJpaRepository;
import java.time.Instant;
import java.util.UUID;
import org.hexagon.core.vo.PaymentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestConfig.class)
class ContractInternalServiceTest {
    @Autowired
    ContractService contractService;
    @Autowired
    private ContractJpaRepository contractJpaRepository;

    @AfterEach
    void tearDown() {
        contractJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("freelancer 역할 해제만 가능한 계약이 존재하는 상황에서 회원 역할 조회 시, isClient는 false, isFreelancer는 true로 반환된다")
    void success_get_member_role_status_given_normal() {
        // given
        String memberCode = "memberCode";

        contractJpaRepository.save(ContractEntity.builder()
                    .code(UUID.randomUUID().toString())
                    .clientCode(memberCode)
                    .freelancerCode(UUID.randomUUID().toString())
                    .commissionCode(UUID.randomUUID().toString())
                    .startedAt(Instant.now())
                    .endedAt(Instant.now())
                    .paymentType(PaymentType.PER_JOB)
                    .unitAmount(100L)
                    .status(ContractStatus.REQUESTED)
                    .name("name")
                    .body("body")
                    .build()); // client인 계약 추가 (하지만 REQUESTED 계약이라 client 역할은 아님)

        contractJpaRepository.save(ContractEntity.builder()
                .code(UUID.randomUUID().toString())
                .clientCode(UUID.randomUUID().toString())
                .freelancerCode(memberCode)
                .commissionCode(UUID.randomUUID().toString())
                .startedAt(Instant.now())
                .endedAt(Instant.now())
                .paymentType(PaymentType.PER_JOB)
                .unitAmount(100L)
                .status(ContractStatus.REQUESTED)
                .name("name")
                .body("body")
                .build()); // freelancer인 계약 추가 (freelancer 역할임)

        // when
        MemberRoleStatusResponse memberRoleStatus = contractService.getMemberRoleStatus(memberCode);

        // then
        assertFalse(memberRoleStatus.isClient());
        assertTrue(memberRoleStatus.isFreelancer());
    }
}
