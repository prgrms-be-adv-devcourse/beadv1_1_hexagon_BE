package com.example.searchservice.commission.service;

import com.example.searchservice.commission.common.PaymentType;
import com.example.searchservice.commission.entity.CommissionDocumentEntity;
import com.example.searchservice.commission.repository.CommissionRepository;
import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommissionInitializer {

    private final CommissionRepository commissionRepository;

    // 테스트용 메소드
    @EventListener(ApplicationReadyEvent.class)
    public void initIndex() {
        commissionRepository.save(CommissionDocumentEntity.builder()
                .code("cms-001")
                .title("Spring 개발자 구합니다.")
                .content("Spring 백엔드 개발 가능하신 분 구합니다.")
                .memberCode("m-001")
                .memberNickname("스프링고수구함")
                .tags(List.of("Spring", "Spring Boot", "Java"))
                .startedAt(LocalDate.now())
                .endedAt(LocalDate.now())
                .paymentType(PaymentType.MONTHLY)
                .payAmount(3_000_000L)
                .isClosed(false)
                .updatedAt(Instant.now())
                .build());

        commissionRepository.save(CommissionDocumentEntity.builder()
                .code("cms-002")
                .title("React 개발자 구합니다.")
                .content("React 프론트엔드 개발 가능하신 분 구합니다.")
                .memberCode("m-002")
                .memberNickname("리액트고수구함")
                .tags(List.of("React", "JavaScript"))
                .startedAt(LocalDate.now())
                .endedAt(LocalDate.now())
                .paymentType(PaymentType.MONTHLY)
                .payAmount(2_500_000L)
                .isClosed(false)
                .updatedAt(Instant.now())
                .build());
    }
}
