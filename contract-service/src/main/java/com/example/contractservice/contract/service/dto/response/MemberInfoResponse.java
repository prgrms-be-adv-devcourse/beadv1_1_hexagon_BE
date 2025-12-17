package com.example.contractservice.contract.service.dto.response;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import java.util.List;

public record MemberInfoResponse(
        List<MemberInfo> internalMemberInfos
) {

    public record MemberInfo (
            String memberCode,
            String nickName,
            MemberRole role
    ) {}

    public enum MemberRole {
        CLIENT,
        FREELANCER,

        @JsonEnumDefaultValue
        ETC
    }
}
