package com.example.contractservice.deposit.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.example.contractservice.deposit.controller.dto.request.DepositRechargeRequest;
import com.example.contractservice.deposit.domain.exception.DepositErrorCode;
import com.example.contractservice.deposit.domain.exception.DepositException;
import com.example.contractservice.deposit.entity.DepositEntity;
import com.example.contractservice.deposit.repository.DepositJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DepositServiceTest {
    @Autowired
    DepositService depositService;
    @Autowired
    DepositJpaRepository depositRepository;

    @AfterEach
    void tearDown() {
        depositRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("이미 존재하는 사용자 예치금에 금액을 제대로 충전할 수 있다.")
    void success_recharge_given_normal() {
        // given
        Long amount = 20_000L;
        String memberCode = "example-member-code";
        depositRepository.save(DepositEntity.createBy(memberCode));

        // when
        depositService.recharge(new DepositRechargeRequest(memberCode, amount));

        // then
        DepositEntity entity = depositRepository.findByMemberCode(memberCode).get();

        assertEquals(amount, entity.getAmount());
    }

    @Test
    @DisplayName("없는 사용자에 대해 충전은 일어날 수 없다.")
    void fail_recharge_given_not_exists_member() {
        // given
        Long amount = 20_000L;
        String memberCode = "example-member-code";

        // when then
        assertThatThrownBy(() -> depositService.recharge(new DepositRechargeRequest(memberCode, amount)))
                .isInstanceOf(DepositException.class)
                .satisfies(ex -> {
                    DepositException depositException = (DepositException) ex;
                    assertEquals(DepositErrorCode.NO_DEPOSIT_ENTITY, depositException.errorCode);
                });
    }

    @Test
    @DisplayName("첫 회원가입한 사용자에 대해 예치금을 생성할 수 있다.")
    void success_create_deposit_given_normal_member() {
        // given
        String memberCode = "example-member-code";

        // when
        depositService.createDeposit(memberCode);

        // then
        assertDoesNotThrow(() -> depositRepository.findByMemberCode(memberCode));
        DepositEntity entity = depositRepository.findByMemberCode(memberCode).get();
        assertEquals(memberCode, entity.getMemberCode().strip());
    }
}
