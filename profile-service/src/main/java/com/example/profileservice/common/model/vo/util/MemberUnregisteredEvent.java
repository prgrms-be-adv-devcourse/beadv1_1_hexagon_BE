package com.example.profileservice.common.model.vo.util;

import java.time.LocalDateTime;

public record MemberUnregisteredEvent(
        String memberCode,
        LocalDateTime timestam
) {

}
