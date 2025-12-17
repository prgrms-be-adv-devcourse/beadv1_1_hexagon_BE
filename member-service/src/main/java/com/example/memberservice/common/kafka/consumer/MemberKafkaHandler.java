package com.example.memberservice.common.kafka.consumer;

import com.example.memberservice.member.mapper.MemberServiceInputMapper;
import com.example.memberservice.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.events.selfpromotion.SelfPromotionUpsertEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberKafkaHandler {

    private final MemberService memberService;

    @KafkaListener(
        topics = "${kafka.topic.profile.update-freelancer-role-topic}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleFreelancerRoleUpdate(SelfPromotionUpsertEvent event) {

        memberService.updateMemberRoleState(
            MemberServiceInputMapper.toUpdateMemberRoleStateInput(event)
        );
    }

}
