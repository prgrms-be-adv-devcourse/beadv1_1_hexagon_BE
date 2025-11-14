package com.example.searchservice.selfpromotion.service;

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
public class SelfPromotionInitializer {

    private final RestTemplate restTemplate;
    private final SelfPromotionRepository selfPromotionRepository;

    @Value("${external.profile-service.url}")
    private String profileServiceUrl;

//    @EventListener(ApplicationReadyEvent.class)
//    public void initIndex() {
//        String getAllSelfPromotionsUrl = profileServiceUrl + "/api/self-promotions"; // TODO : 실제 컨트롤러 완성되면 URL 수정
//        ResponseEntity<List<SelfPromotionDto>> response = restTemplate.exchange(
//                getAllSelfPromotionsUrl,
//                HttpMethod.GET,
//                null,
//                new ParameterizedTypeReference<>() {
//                }
//        );
//
//        List<SelfPromotionDto> selfPromotions = response.getBody();
//        if (selfPromotions == null || selfPromotions.isEmpty()) {
//            throw new SelfPromotionException(SelfPromotionErrorCode.SELF_PROMOTION_FETCH_FAILED);
//        }
//
//        List<SelfPromotionDocumentEntity> selfPromotionDocs = selfPromotions.stream()
//                .map(SelfPromotionMapper::toSelfPromotionDocument)
//                .toList();
//
//        selfPromotionRepository.saveAll(selfPromotionDocs);
//    }

    // 테스트용 메소드
    @EventListener(ApplicationReadyEvent.class)
    public void initIndex() {
        selfPromotionRepository.save(SelfPromotionDocumentEntity.builder()
                .code("sp-001")
                .title("Spring 개발자 필요하신 분")
                .content("Spring 개발 경험 다수 보유 중입니다.")
                .memberCode("m-001")
                .memberNickname("스프링고수")
                .updatedAt(Instant.now())
                .build());

        selfPromotionRepository.save(SelfPromotionDocumentEntity.builder()
                .code("sp-002")
                .title("React 개발자 필요하신 분")
                .content("React 개발 경험 다수 보유 중입니다.")
                .memberCode("m-002")
                .memberNickname("리액트고수")
                .updatedAt(Instant.now())
                .build());
    }
}
