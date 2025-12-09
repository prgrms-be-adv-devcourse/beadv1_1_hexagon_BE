package com.example.memberservice.member.service.model.vo;

import com.example.memberservice.member.model.enums.MemberRole;

public record InternalMemberInfo(
    String memberCode,
    String nickName,
    MemberRole role
) {

}