package com.example.memberservice.auth.email.controller.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;

@Schema(description = "이메일 인증 요청 dto")
public record CreateEmailAuthRequest(
    @Schema(description = "이메일 인증을 진행할 email 값", defaultValue = "aaaaaa@example.com")
    @NotBlank(message = "이메일은 필수 값입니다.")
    @Email(message = "이메일 형식이 틀렸습니다.")
    @Pattern(
        regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "올바른 이메일 형식이 아닙니다."
    )
    String email
) {

}
