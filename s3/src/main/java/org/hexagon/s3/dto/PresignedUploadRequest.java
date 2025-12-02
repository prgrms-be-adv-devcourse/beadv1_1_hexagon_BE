package org.hexagon.s3.dto;

public record PresignedUploadRequest(
        ServiceName serviceName,
        String fileName,
        String contentType
) {

}
