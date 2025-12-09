package org.hexagon.s3service.dto;

public record ExistsResponse(
        String key,
        Boolean exists
) {

}
