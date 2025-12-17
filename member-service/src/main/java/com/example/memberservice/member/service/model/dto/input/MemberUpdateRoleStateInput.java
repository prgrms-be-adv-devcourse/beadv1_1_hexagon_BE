package com.example.memberservice.member.service.model.dto.input;

import com.example.memberservice.member.model.enums.MemberRole;

public record MemberUpdateRoleStateInput(
    String memberCode,
    MemberRole role
) {

}
