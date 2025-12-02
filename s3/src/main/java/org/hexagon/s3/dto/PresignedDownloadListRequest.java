package org.hexagon.s3.dto;

import java.util.List;

public record PresignedDownloadListRequest(
        List<String> keys
) {

}
