package com.example.profileservice.common.model.vo.util;

public record InternalMemberInfo(
    String memberCode,
    String nickName,
    MemberRole role

) {
    enum MemberRole {
        NONE, CLIENT, FREELANCER, BOTH, ADMIN
    }

}