package com.example.memberservice.member.mapper;

import com.example.memberservice.member.controller.dto.request.MemberCreateRequest;
import com.example.memberservice.member.controller.dto.request.MemberRoleUpdateRequest;
import com.example.memberservice.member.controller.dto.request.MemberUpdateRequest;
import com.example.memberservice.member.service.model.dto.input.MemberCreateInput;
import com.example.memberservice.member.service.model.dto.input.MemberDeleteInput;
import com.example.memberservice.member.service.model.dto.input.MemberExistByNameInput;
import com.example.memberservice.member.service.model.dto.input.MemberGetInput;
import com.example.memberservice.member.service.model.dto.input.MemberUpdateInput;
import com.example.memberservice.member.service.model.dto.input.MemberUpdateWorkStateInput;

public final class MemberServiceInputMapper {

    private MemberServiceInputMapper() {
    }

    public static MemberGetInput toGetMemberInput(String xCode, String paramCode) {
        return new MemberGetInput(xCode, paramCode);
    }

    public static MemberCreateInput toCreateMemberInput(String memberCode,
        MemberCreateRequest request) {
        return new MemberCreateInput(memberCode, request.name(), request.phoneNumber(),
            request.birthDate(), request.gender());
    }

    public static MemberUpdateInput toUpdateMemberInput(String memberCode,
        MemberUpdateRequest request) {
        return new MemberUpdateInput(memberCode, request.name(), request.phoneNumber(),
            request.birthDate(), request.gender());
    }

    public static MemberUpdateWorkStateInput toUpdateMemberRoleStateInput(String memberCode,
        MemberRoleUpdateRequest request) {
        return new MemberUpdateWorkStateInput(memberCode,request.memberRole());
    }

    public static MemberDeleteInput toDeleteMemberInput(String memberCode) {
        return new MemberDeleteInput(memberCode);
    }

    public static MemberExistByNameInput toExistMemberByNameInput(String memberCode, String name) {
        return new MemberExistByNameInput(memberCode, name);
    }
}
