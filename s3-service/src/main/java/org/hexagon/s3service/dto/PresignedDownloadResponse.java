package org.hexagon.s3service.dto;

import org.hexagon.s3service.vo.FileType;

public record PresignedDownloadResponse(
        String key,
        String queryString,
        FileType fileType
) {

}
