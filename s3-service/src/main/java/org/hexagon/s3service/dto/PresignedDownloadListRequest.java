package org.hexagon.s3service.dto;

import java.util.List;

public record PresignedDownloadListRequest(
        List<String> keys
) {

}
