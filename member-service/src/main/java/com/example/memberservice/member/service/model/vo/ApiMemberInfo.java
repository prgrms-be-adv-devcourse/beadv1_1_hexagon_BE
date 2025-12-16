package com.example.memberservice.member.service.model.vo;


import com.example.memberservice.member.model.enums.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;

public record ApiMemberInfo(
    @Schema(description = "사용자 외부 식별자", defaultValue = "UUID")
    String code,


    @Schema(description = "사용자 닉네임", defaultValue = "이어드림 팬 1")
    String nickName,

    @Schema(description = "사용자 이메일", defaultValue = "devthkim0317@gmail.com")
    String email,

    @Schema(description = "사용자 전화번호", defaultValue = "010-0000-0000")
    String phoneNumber,

    @Schema(description = "사용자 생일정보", defaultValue = "YYYY-MM-DD")
    String birthDay,

    @Schema(description = "사용자 성별", defaultValue = "MAN OR FEMAIL")
    String gender,

    @Schema(description = "사용자 역할", defaultValue = "FREELANCER")
    MemberRole role

) {

}
