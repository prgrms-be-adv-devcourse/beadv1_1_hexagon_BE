package com.example.cartpostservice.commissions.controller.dto.response.internal;

import org.hexagon.core.vo.FileType;

public record PresignedUrlComponent(
        String key,
        String queryString,
        FileType fileType
) {

}
