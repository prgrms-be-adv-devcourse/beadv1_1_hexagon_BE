package com.example.communicationservice.controller.dto.response;

public record ChatFileUploadUrlGenerateResponse(
    String key, // pre-signed upload URL 구성 요소
    String queryString // pre-signed upload URL 구성 요소
) {
}
