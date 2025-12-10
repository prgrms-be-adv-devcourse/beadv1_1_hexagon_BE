package com.example.communicationservice.client.dto.input;

public record FileUploadUrlGenerateInput(
    String serviceName,
    String fileName,
    String contentType
) {
}
