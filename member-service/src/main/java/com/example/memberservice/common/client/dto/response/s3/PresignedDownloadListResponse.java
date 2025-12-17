package com.example.memberservice.common.client.dto.response.s3;

import java.util.List;

public record PresignedDownloadListResponse(
        List<PresignedDownloadResponse> urls
) {

}
