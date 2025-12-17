package com.example.profileservice.common.model.vo.util;

import org.hexagon.core.events.member.MemberDeletedEvent;
import org.hexagon.core.events.member.MemberDeletedFreelancerRoleEvent;
import com.example.profileservice.resume.service.ResumeService;
import com.example.profileservice.selfPromotion.service.SelfPromotionService;
import com.example.profileservice.tag.service.TagService;
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
    private final ResumeService resumeService;
    private final TagService tagService;
    private final ObjectMapper objectMapper;

    // 멤버 탈퇴 이벤트 수신 토픽
    private static final String UNREGISTER_TOPIC = "${kafka.topic.member-unregister.name}";
    private static final String ROLE_REVOKE_TOPIC = "${kafka.topic.member-role-revoke.name}";

    // 1. 회원 탈퇴 이벤트 처리
    @KafkaListener(topics = UNREGISTER_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUnregisterEvent(String message) {
        MemberUnregisteredEvent event = null;
        try {
            event = objectMapper.readValue(message, MemberDeletedEvent.class);
            String memberCode = event.memberCode();

            // 1. Tag 연결 정보 Hard Delete (일괄 삭제)
            tagService.deleteMemberTagsByMemberCode(memberCode);
            log.info("Successfully hard deleted all member tags for memberCode: {}", memberCode);

            // 2. Resume (이력서 및 경험) Soft Delete
            resumeService.deleteResumesByMemberCode(memberCode);
            log.info("Successfully soft deleted all resumes for memberCode: {}", memberCode);

            // 3. Self Promotion Soft Delete
            selfPromotionService.deletePromotionsByMemberCode(memberCode);
            log.info("Successfully soft deleted all self promotions for memberCode: {}", memberCode);

        } catch (JsonProcessingException e) {
            log.error("Failed to parse Unregister Event JSON: {}", message, e);
            // JSON 파싱 실패는 재시도 필요 없으므로 return
            return;
        } catch (Exception e) {
            // NullPointerException 방지를 위해 event 확인 추가
            log.error("Failed to process Deleted Event for memberCode {}. Error: {}", event != null ? event.memberCode() : "N/A", e.getMessage(), e);
            // 시스템 오류는 재시도를 위해 throw (Kafka Consumer 설정에 따라 자동 재시도됨)
            throw new RuntimeException("Error processing unregister event.", e);
        }
    }

    // 2. Role 해제 이벤트 처리 (Role 해제 시에도 동일하게 작동해야 한다면)
    @KafkaListener(topics = ROLE_REVOKE_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeRoleRevokeEvent(String message) {
        MemberDeletedFreelancerRoleEvent event = null;
        try {
            event = objectMapper.readValue(message, MemberDeletedFreelancerRoleEvent.class);
            String memberCode = event.memberCode();
            log.info("Freelancer Role Delete Event received for memberCode: {}", memberCode);

            // 프리랜서 역할 해제 시에는 일반적으로 셀프 프로모션과 이력서만 삭제/비활성화 처리합니다.
            // 1. Resume (이력서 및 경험) Soft Delete
            resumeService.deleteResumesByMemberCode(memberCode);
            log.info("Successfully soft deleted all resumes (due to role revoke) for memberCode: {}", memberCode);

            // 2. Self Promotion Soft Delete
            selfPromotionService.deletePromotionsByMemberCode(memberCode);
            log.info("Successfully soft deleted all self promotions (due to role revoke) for memberCode: {}", memberCode);

        } catch (JsonProcessingException e) {
            log.error("Failed to parse Role Delete Event JSON: {}", message, e);
        } catch (Exception e) {
            // NullPointerException 방지를 위해 event 확인 추가
            log.error("Failed to process Role Delete Event for memberCode {}. Error: {}", event != null ? event.memberCode() : "N/A", e.getMessage(), e);
            throw new RuntimeException("Error processing role revoke event.", e);
        }
    }
}
