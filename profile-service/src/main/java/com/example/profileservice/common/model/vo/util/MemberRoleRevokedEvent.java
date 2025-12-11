package com.example.profileservice.common.model.vo.util;

import java.time.LocalDateTime;

public record MemberRoleRevokedEvent(
        String memberCode,
        LocalDateTime timestamp,
        String role
) {

}
