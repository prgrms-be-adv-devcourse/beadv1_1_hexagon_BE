package com.example.profileservice.common.model.vo.util;

import java.time.Instant;

public record MemberUnregisteredEvent(
        String memberCode,
        Instant timestamp
) {

}
