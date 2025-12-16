package com.example.memberservice.member.service.model.dto.input;

import com.example.memberservice.member.model.enums.Gender;
import java.time.LocalDate;

public record MemberUpdateInput(
    String memberCode,

    String name,

    String phoneNumber,

    LocalDate birthDate,

    Gender gender,

    String profileImageKey
) {

}
