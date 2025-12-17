package com.example.cartpostservice.commissions.infra.client.internal.dto.response;

import com.example.cartpostservice.commissions.common.dto.PresignedUrlComponent;
import java.util.List;

public record DownloadFileComponentResponse(
        List<PresignedUrlComponent> urls
) {

}
