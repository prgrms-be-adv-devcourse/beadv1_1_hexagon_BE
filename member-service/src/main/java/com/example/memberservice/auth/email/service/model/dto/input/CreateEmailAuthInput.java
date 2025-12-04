package com.example.memberservice.auth.email.service.model.dto.input;

import com.example.memberservice.member.model.enums.MemberRole;

public record CreateEmailAuthInput(
    MemberRole memberRole,
    String memberCode,
    String to
) {

}
