package com.example.cartpostservice.commissions.controller.dto.request.internal;

import org.hexagon.core.vo.ServiceName;

public record DownloadFileComponentRequest(
        ServiceName serviceName,
        String code
) {

}
