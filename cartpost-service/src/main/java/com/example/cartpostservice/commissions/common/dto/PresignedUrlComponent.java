package com.example.cartpostservice.commissions.common.dto;

import org.hexagon.core.vo.FileType;

public record PresignedUrlComponent(
        String key,
        String queryString,
        FileType fileType
) {

}
