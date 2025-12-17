package com.example.cartpostservice.commissions.controller.external.dto.response;

import com.example.cartpostservice.commissions.common.dto.PresignedUrlComponent;
import java.util.List;

public record FileComponentReadResponse(
        List<PresignedUrlComponent> urls
) {

}
