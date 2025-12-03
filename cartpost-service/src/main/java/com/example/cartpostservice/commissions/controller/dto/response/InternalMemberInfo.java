package com.example.cartpostservice.commissions.controller.dto.response;

public record InternalMemberInfo(
        String memberCode,
        String nickName,
        boolean canWork
) {

}
