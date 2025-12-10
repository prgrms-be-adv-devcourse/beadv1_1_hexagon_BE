package com.example.cartpostservice.commissions.infra.client.internal.dto.response;

import org.hexagon.core.vo.FileType;

public record PresignedUrlComponent(
        String key,
        String queryString,
        FileType fileType
) {

}
