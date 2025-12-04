package org.hexagon.s3service.dto;

import org.hexagon.s3service.vo.ServiceName;

public record PresignedDownloadRequestByCode(
        ServiceName serviceName,
        String code
) {

}
