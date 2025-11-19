package com.example.searchservice.selfpromotion.service;

import com.example.searchservice.common.vo.PaymentType;
import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;
import com.example.searchservice.selfpromotion.repository.SelfPromotionRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
// 더미 데이터 삽입용 클래스
public class SelfPromotionInitializer {

    private final SelfPromotionRepository selfPromotionRepository;

    // 테스트용 메소드
    @EventListener(ApplicationReadyEvent.class)
    public void initIndex() {
        selfPromotionRepository.save(SelfPromotionDocumentEntity.builder()
                        .code("sp-001")
                        .title("Spring 개발자 필요하신 분")
                        .content("Spring 개발 경험 다수 보유 중입니다.")
                        .memberCode("m-001")
                        .memberNickname("스프링고수")
                        .paymentType(PaymentType.MONTHLY)
                        .payAmount(3_000_000L)
                        .updatedAt(Instant.now())
                        .build());

        selfPromotionRepository.save(SelfPromotionDocumentEntity.builder()
                        .code("sp-002")
                        .title("React 개발자 필요하신 분")
                        .content("React 개발 경험 다수 보유 중입니다.")
                        .memberCode("m-002")
                        .memberNickname("리액트고수")
                        .paymentType(PaymentType.MONTHLY)
                        .payAmount(4_000_000L)
                        .updatedAt(Instant.now())
                        .build());
    }
}
