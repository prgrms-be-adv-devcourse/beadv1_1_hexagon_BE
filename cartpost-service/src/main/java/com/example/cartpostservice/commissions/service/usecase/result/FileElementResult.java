package com.example.cartpostservice.commissions.service.usecase.result;

import com.example.cartpostservice.commissions.common.dto.PresignedUrlComponent;
import java.util.List;

public record FileElementResult(
        List<PresignedUrlComponent> urls
) {

}
