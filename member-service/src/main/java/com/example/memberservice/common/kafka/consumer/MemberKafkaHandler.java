package com.example.memberservice.common.kafka.consumer;

import com.example.memberservice.member.model.enums.MemberRole;
import com.example.memberservice.member.service.MemberService;
import com.example.memberservice.member.service.model.dto.input.MemberUpdateInput;
import com.example.memberservice.member.service.model.dto.input.MemberUpdateRoleStateInput;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberKafkaHandler {

    private final MemberService memberService;

    //TODO(이후 profile 모듈 카프카 전송 메서드 생성시 해당 Event 객체 참조하도록 수정)
//    @KafkaListener(
//        topics = "${kafka.topic.profile.freelancer-role-update-topic}",
//        groupId = "${spring.kafka.consumer.group-id}",
//        containerFactory = "kafkaListenerContainerFactory"
//    )
//    public void handleFreelancerRoleUpdate(TempEventDto event) {
//
//        memberService.updateMember(
//            new MemberUpdateRoleStateInput(
//                MemberRole.FREELANCER,
//                event.getMemberCode()
//            )
//        );
//    }

}
