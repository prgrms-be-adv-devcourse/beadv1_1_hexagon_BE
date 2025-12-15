package com.example.profileservice.common.model.vo.util;

import java.time.Instant;

public record MemberRoleRevokedEvent(
        String memberCode,
        Instant timestamp,
        String role
) {

}
