package org.hexagon.s3service.dto;

import org.hexagon.core.vo.ServiceName;

public record PresignedDownloadRequestByCode(
        ServiceName serviceName,
        String code
) {

}
