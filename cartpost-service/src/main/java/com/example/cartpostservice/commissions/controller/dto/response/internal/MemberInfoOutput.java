package com.example.cartpostservice.commissions.controller.dto.response.internal;

import java.util.List;

public record MemberInfoOutput(
        List<InternalMemberInfo> internalMemberInfos
) {

}
