package com.example.cartpostservice.commissions.infra.client.internal.dto.response;

public record InternalMemberInfo(
        String memberCode,
        String nickName,
        boolean canWork
) {

}
