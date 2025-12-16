package com.example.memberservice.common.kafka.consumer;

import com.example.memberservice.member.mapper.MemberServiceInputMapper;
import com.example.memberservice.member.model.enums.MemberRole;
import com.example.memberservice.member.service.MemberService;
import com.example.memberservice.member.service.model.dto.input.MemberUpdateInput;
import com.example.memberservice.member.service.model.dto.input.MemberUpdateRoleStateInput;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.events.selfpromotion.SelfPromotionCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
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
    public void handleFreelancerRoleUpdate(SelfPromotionCreatedEvent event) {

        memberService.updateMemberRoleState(
            MemberServiceInputMapper.toUpdateMemberRoleStateInput(event)
        );
    }

}
