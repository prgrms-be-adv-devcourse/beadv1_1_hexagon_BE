package com.example.profileservice.common.model.vo.util;

import com.example.profileservice.selfPromotion.service.SelfPromotionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class MemberEventConsumer {
    private final SelfPromotionService selfPromotionService;
    private final ObjectMapper objectMapper;

    // 멤버 탈퇴 이벤트 수신 토픽
    private static final String UNREGISTER_TOPIC = "${kafka.topic.member-unregister.name}";
    private static final String ROLE_REVOKE_TOPIC = "${kafka.topic.member-role-revoke.name}";

    // 1. 회원 탈퇴 이벤트 처리
    @KafkaListener(topics = UNREGISTER_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUnregisterEvent(String message) {
        MemberUnregisteredEvent event = null;
        try {
            event = objectMapper.readValue(message, MemberUnregisteredEvent.class);
            log.info("Member Unregister Event received for memberCode: {}", event.memberCode());

            // 비즈니스 로직 호출
            selfPromotionService.deletePromotionsByMemberCode(event.memberCode());
            log.info("Successfully soft deleted all self promotions for memberCode: {}", event.memberCode());

        } catch (JsonProcessingException e) {
            log.error("Failed to parse Unregister Event JSON: {}", message, e);
            // JSON 파싱 실패는 재시도 필요 없으므로 return
            return;
        } catch (Exception e) {
            log.error("Failed to process Unregister Event for memberCode {}. Error: {}", event.memberCode(), e.getMessage(), e);
            // 시스템 오류는 재시도를 위해 throw (Kafka Consumer 설정에 따라 자동 재시도됨)
            throw new RuntimeException("Error processing unregister event.", e);
        }
    }

    // 2. Role 해제 이벤트 처리 (Role 해제 시에도 동일하게 작동해야 한다면)
    @KafkaListener(topics = ROLE_REVOKE_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeRoleRevokeEvent(String message) {
        MemberRoleRevokedEvent event = null;
        try {
            event = objectMapper.readValue(message, MemberRoleRevokedEvent.class);

            // 프리랜서 Role 해제 시에만 처리
            if ("FREELANCER".equalsIgnoreCase(event.role())) {
                log.info("Freelancer Role Revoke Event received for memberCode: {}", event.memberCode());
                selfPromotionService.deletePromotionsByMemberCode(event.memberCode());
                log.info("Successfully soft deleted all self promotions (due to role revoke) for memberCode: {}", event.memberCode());
            }

        } catch (JsonProcessingException e) {
            log.error("Failed to parse Role Revoke Event JSON: {}", message, e);
        } catch (Exception e) {
            log.error("Failed to process Role Revoke Event for memberCode {}. Error: {}", event.memberCode(), e.getMessage(), e);
            throw new RuntimeException("Error processing role revoke event.", e);
        }
    }
}
