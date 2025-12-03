package org.hexagon.s3.dto;

import java.util.List;

public record PresignedDownloadListResponse(
        List<String> urls
) {

}
