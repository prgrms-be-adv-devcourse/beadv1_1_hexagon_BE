package com.example.memberservice.member.controller.dto.request;

import com.example.memberservice.member.model.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record MemberUpdateRequest(

    @Schema(description = "사용자 닉네임", example = "이어드림1호팬")
    String name,

    @Schema(description = "사용자 전화번호", example = "010-9343-7373")
    String phoneNumber,

    @Schema(description = "사용자 생년월일", example = "2000-03-17")
    LocalDate birthDate,

    @Schema(description = "사용자 성별", example = "MAN OR FEMALE")
    Gender gender
) {

}
