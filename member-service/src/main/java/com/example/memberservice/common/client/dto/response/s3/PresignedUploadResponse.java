package com.example.memberservice.common.client.dto.response.s3;

public record PresignedUploadResponse(
        String key,
        String queryString
) {

}
