package com.example.memberservice.member.service.model.dto.input;

import com.example.memberservice.member.model.enums.MemberRole;

public record MemberUpdateWorkStateInput(
    String memberCode,
    MemberRole role
) {

}
