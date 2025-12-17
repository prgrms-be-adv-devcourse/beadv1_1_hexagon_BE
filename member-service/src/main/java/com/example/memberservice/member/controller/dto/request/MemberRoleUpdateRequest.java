package com.example.memberservice.member.controller.dto.request;

import com.example.memberservice.member.model.enums.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "변경할 멤버 상태를 담습니다.")
public record MemberRoleUpdateRequest(
    @Schema(description = "변경 할 멤버 상태", allowableValues = {"CLIENT","FREELANCER"})
    MemberRole role
) {

}
