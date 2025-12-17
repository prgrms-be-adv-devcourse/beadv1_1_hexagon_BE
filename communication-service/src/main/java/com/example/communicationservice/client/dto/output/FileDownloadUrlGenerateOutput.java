package com.example.communicationservice.client.dto.output;

import org.hexagon.core.vo.FileType;

public record FileDownloadUrlGenerateOutput(
    String key,
    String queryString,
    FileType fileType
) {
}
