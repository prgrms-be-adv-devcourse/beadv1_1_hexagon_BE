package org.hexagon.s3service.dto;

import org.hexagon.core.vo.FileType;

public record PresignedDownloadResponse(
        String key,
        String queryString,
        FileType fileType
) {

}
