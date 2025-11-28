package com.example.cartpostservice.commissions.controller.dto.response;

import java.util.List;

public record MemberInfoOutput(
        List<InternalMemberInfo> internalMemberInfos
) {

}
