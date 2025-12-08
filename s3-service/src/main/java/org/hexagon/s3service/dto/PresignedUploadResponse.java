package org.hexagon.s3service.dto;

public record PresignedUploadResponse(
        String key,
        String queryString
) {

}
