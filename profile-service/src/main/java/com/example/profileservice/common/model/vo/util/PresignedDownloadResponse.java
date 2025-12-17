package com.example.profileservice.common.model.vo.util;

import org.hexagon.core.vo.FileType;

public record PresignedDownloadResponse(
        String key,
        String queryString,
        FileType fileType
) {

}
