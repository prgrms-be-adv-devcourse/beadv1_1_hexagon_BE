package com.example.memberservice.common.client.dto.response.s3;

import org.hexagon.core.vo.FileType;

public record PresignedDownloadResponse(
        String key,
        String queryString,
        FileType fileType
) {

}
