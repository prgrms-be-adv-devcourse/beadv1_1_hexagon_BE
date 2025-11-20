package com.example.profileservice.common.model.vo;

import com.example.profileservice.selfPromotion.model.dto.response.SelfPromotionResponse;
import com.example.profileservice.selfPromotion.repository.SelfPromotionRepository;
import com.example.profileservice.selfPromotion.service.SelfPromotionService;
import com.example.profileservice.tag.repository.TagRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.events.selfpromotion.SelfPromotionInitEvent;
import org.hexagon.core.events.tag.TagInitEvent;
import org.hexagon.core.vo.SelfPromotion;
import org.hexagon.core.vo.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProfileDataInitializer {

    private final SelfPromotionRepository selfPromotionRepository;
    private final TagRepository tagRepository;
    private final KafkaProducer kafkaProducer;
    private final SelfPromotionService selfPromotionService;

    // 토픽 이름 설정 (application.yml 또는 default 값 사용)
    @Value("${topics.selfpromotion-init-events:selfpromotion-init-events}")
    private String selfPromotionInitTopic;

    @Value("${topics.tag-init-events:tag-init-events}")
    private String tagInitTopic;

    // 애플리케이션이 완전히 준비된 후 (DB 연결, 빈 초기화 완료) 초기화 이벤트를 발행합니다.
    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public void sendInitialDataEvents() {
        log.info("어플리케이션이 준비되었습니다. 초기 데이터 동기화 이벤트를 보내는 중...");

        // 1. Self Promotion 초기화 이벤트 전송
        sendInitialSelfPromotionData();

        // 2. Tag 초기화 이벤트 전송
        sendInitialTagData();

        log.info("초기 데이터 동기화 이벤트가 성공적으로 전송되었습니다.");
    }

    // Self Promotion 전체 데이터를 조회하여 Kafka에 전송
    private void sendInitialSelfPromotionData() {
        // 1. Soft Delete 되지 않은 모든 Self Promotion 엔티티 조회 (기존 로직 유지)
        List<SelfPromotionResponse> allPromotions = selfPromotionService.getAllPromotions();

        // 2. 코어 모듈 VO (SelfPromotion)로 변환
        List<SelfPromotion> selfPromotionVoList = allPromotions.stream()
                // SelfPromotionResponse -> SelfPromotion VO로 변환
                .map(response -> new SelfPromotion(
                        response.promotionCode(),
                        response.title(),
                        response.content(),
                        response.memberCode(),
                        response.memberNickname(),
                        response.paymentType(),
                        response.unitAmount(),
                        response.updatedAt()
                ))
                .collect(Collectors.toList());

        // 3. 코어 모듈의 SelfPromotionInitEvent 생성
        SelfPromotionInitEvent event = new SelfPromotionInitEvent(selfPromotionVoList);

        // 4. Kafka에 전송 (키 없이 초기 동기화 이벤트 발행)
        kafkaProducer.send(selfPromotionInitTopic, event);
        log.info("  -> {}개의 SelfPromotion 레코드를 토픽: {}로 보냈습니다.", selfPromotionVoList.size(), selfPromotionInitTopic);
    }

    // Tag 전체 데이터를 조회하여 Kafka에 전송
    private void sendInitialTagData() {
        // 모든 Tag 엔티티 조회
        List<Tag> allTags = tagRepository.findAll().stream()
                .map(tag -> new Tag(tag.getCode(), tag.getSkill()))
                .collect(Collectors.toList());

        TagInitEvent event = new TagInitEvent(allTags);

        kafkaProducer.send(tagInitTopic, event);
        log.info("  -> Sent {} Tag records to topic: {}", allTags.size(), tagInitTopic);
    }
}