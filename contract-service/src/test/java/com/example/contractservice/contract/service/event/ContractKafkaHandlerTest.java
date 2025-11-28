package com.example.contractservice.contract.service.event;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.contractservice.common.PaymentType;
import com.example.contractservice.contract.common.ContractStatus;
import com.example.contractservice.contract.entity.ContractEntity;
import com.example.contractservice.contract.repository.ContractJpaRepository;
import com.example.contractservice.contract.service.ContractEventService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class ContractKafkaHandlerTest {
    @Autowired
    ContractJpaRepository contractRepository;
    @Autowired
    ContractEventService contractEventService;

    @AfterEach
    void tearDown() {
        contractRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("장바구니 삭제 이벤트 수신 후 계약을 취소 상태로 만들 수 있다.")
    void success_status_Change_to_cancelled_given_normal() {
        // given

        String requestorCode = UUID.randomUUID().toString();
        ContractEntity entity = ContractEntity.builder()
                .code(UUID.randomUUID().toString())
                .requestorCode(requestorCode)
                .contractorCode(UUID.randomUUID().toString())
                .freelancerCode(requestorCode)
                .name("이름")
                .body("내용")
                .status(ContractStatus.CONFIRMED)
                .startedAt(Instant.now())
                .endedAt(Instant.now().plusSeconds(10000000))
                .unitAmount(20000000L)
                .paymentType(PaymentType.MONTHLY)
                .build();
        ContractEntity saved = contractRepository.save(entity);

        // when
        contractEventService.cancelContract(saved.getCode());

        // then
        ContractEntity resEntity = contractRepository.findByCode(saved.getCode()).get();

        assertEquals(ContractStatus.CANCELLED, resEntity.getStatus());
    }
}
