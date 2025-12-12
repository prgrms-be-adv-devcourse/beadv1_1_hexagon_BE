package com.example.communicationservice.client.dto.output;

import java.util.List;

public record FileDownloadUrlListGenerateOutput(
    List<FileDownloadUrlGenerateOutput> urls
) {
}
