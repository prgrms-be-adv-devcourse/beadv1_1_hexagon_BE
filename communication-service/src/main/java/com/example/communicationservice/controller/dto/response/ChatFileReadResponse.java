package com.example.communicationservice.controller.dto.response;

import org.hexagon.core.vo.FileType;

public record ChatFileReadResponse(
    String key,
    String queryString, // pre-signed download URL 구성 요소
    FileType fileType
) {
}
