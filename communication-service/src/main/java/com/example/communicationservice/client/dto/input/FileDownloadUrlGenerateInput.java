package com.example.communicationservice.client.dto.input;

import java.util.List;

public record FileDownloadUrlGenerateInput(
    List<String> keys
) {
}
