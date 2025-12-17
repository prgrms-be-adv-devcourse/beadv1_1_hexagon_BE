package com.example.communicationservice.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChatFileUploadUrlGenerateRequest(

    @NotBlank(message = "파일 이름은 필수입니다.")
    String fileName,

    @NotBlank(message = "파일 타입은 필수입니다.")
    @Pattern(
        regexp = "^(image/(jpeg|png|webp)|application/pdf)$", // 이미지, PDF만 가능
        message = "허용되지 않는 파일 타입입니다."
    )
    String contentType

) {
}
