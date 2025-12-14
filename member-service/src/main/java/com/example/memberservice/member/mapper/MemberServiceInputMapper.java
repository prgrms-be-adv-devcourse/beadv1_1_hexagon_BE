package com.example.memberservice.member.mapper;

import com.example.memberservice.member.controller.dto.request.MemberCreateRequest;
import com.example.memberservice.member.controller.dto.request.MemberRoleUpdateRequest;
import com.example.memberservice.member.controller.dto.request.MemberUpdateRequest;
import com.example.memberservice.member.model.enums.MemberRole;
import com.example.memberservice.member.service.model.dto.input.MemberCreateInput;
import com.example.memberservice.member.service.model.dto.input.MemberDeleteInput;
import com.example.memberservice.member.service.model.dto.input.MemberExistByNameInput;
import com.example.memberservice.member.service.model.dto.input.MemberGetInput;
import com.example.memberservice.member.service.model.dto.input.MemberUpdateInput;
import com.example.memberservice.member.service.model.dto.input.MemberUpdateRoleStateInput;
import org.hexagon.core.events.selfpromotion.SelfPromotionCreatedEvent;

public final class MemberServiceInputMapper {

    private MemberServiceInputMapper() {
    }

    public static MemberGetInput toGetMemberInput(String xCode, String paramCode) {
        return new MemberGetInput(xCode, paramCode);
    }

    public static MemberCreateInput toCreateMemberInput(String memberCode,
        MemberCreateRequest request) {
        return new MemberCreateInput(
            memberCode,
            request.name(),
            request.phoneNumber(),
            request.birthDate(),
            request.gender(),
            request.profileImageKey()
        );
    }

    public static MemberUpdateInput toUpdateMemberInput(String memberCode,
        MemberUpdateRequest request) {
        return new MemberUpdateInput(
            memberCode,
            request.name(),
            request.phoneNumber(),
            request.birthDate(),
            request.gender(),
            request.profileImageKey()
        );
    }

    public static MemberUpdateRoleStateInput toUpdateMemberRoleStateInput(String memberCode,
        MemberRoleUpdateRequest request) {
        return new MemberUpdateRoleStateInput(memberCode, request.role());
    }

    //TODO(Profile 에서 카프카 메세지 구현 이후 의존)
    public static MemberUpdateRoleStateInput toUpdateMemberRoleStateInput(SelfPromotionCreatedEvent event){
        return new MemberUpdateRoleStateInput(event.memberCode(), MemberRole.FREELANCER);
    }

    public static MemberDeleteInput toDeleteMemberInput(String memberCode) {
        return new MemberDeleteInput(memberCode);
    }

    public static MemberExistByNameInput toExistMemberByNameInput(String memberCode, String name) {
        return new MemberExistByNameInput(memberCode, name);
    }
}
