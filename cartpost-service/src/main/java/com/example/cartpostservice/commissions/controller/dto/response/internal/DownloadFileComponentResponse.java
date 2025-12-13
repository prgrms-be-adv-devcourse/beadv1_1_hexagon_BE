package com.example.cartpostservice.commissions.controller.dto.response.internal;

import java.util.List;

public record DownloadFileComponentResponse(
        List<PresignedUrlComponent> urls
) {

}
