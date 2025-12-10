package com.example.communicationservice.client.dto.input;

import org.hexagon.core.vo.ServiceName;

public record FileUploadUrlGenerateInput(
    ServiceName serviceName,
    String fileName,
    String contentType
) {
}
