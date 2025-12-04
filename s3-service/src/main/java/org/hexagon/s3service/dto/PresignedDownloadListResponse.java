package org.hexagon.s3service.dto;

import java.util.List;

public record PresignedDownloadListResponse(
        List<PresignedDownloadResponse> urls
) {

}
