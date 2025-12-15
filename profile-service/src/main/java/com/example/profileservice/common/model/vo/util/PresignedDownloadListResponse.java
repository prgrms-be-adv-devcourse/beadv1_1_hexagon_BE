package com.example.profileservice.common.model.vo.util;

import java.util.List;

public record PresignedDownloadListResponse(
        List<PresignedDownloadResponse> urls
) {

}
