package com.example.cartpostservice.commissions.infra.client.internal.dto.response;

import java.util.List;

public record MemberInfoOutput(
        List<InternalMemberInfo> internalMemberInfos
) {

}
