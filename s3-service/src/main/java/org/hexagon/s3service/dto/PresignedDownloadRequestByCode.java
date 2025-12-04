package org.hexagon.s3service.dto;

public record PresignedDownloadRequestByCode(
        ServiceName serviceName,
        String code
) {

}
