package com.example.memberservice.member.controller.dto.request;

import com.example.memberservice.member.model.enums.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "추가할 멤버 상태를 담습니다.")
public record MemberRoleUpdateRequest(
    @Schema(description = "추가할 멤버 상태", allowableValues = {"CLIENT", "FREELANCER"})
    MemberRole role
) {

}
