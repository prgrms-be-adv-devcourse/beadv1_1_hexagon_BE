package com.example.contractservice.contract.service.dto.response;

import java.util.List;

public record MemberInfoResponse(
        List<MemberInfo> internalMemberInfos
) {

    public record MemberInfo (
            String memberCode,
            String nickName,
            Boolean canWork
    ) {}
}
