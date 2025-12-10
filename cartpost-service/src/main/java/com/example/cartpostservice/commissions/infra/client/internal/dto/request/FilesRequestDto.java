package com.example.cartpostservice.commissions.infra.client.internal.dto.request;

import java.util.List;

public record FilesRequestDto(
        String code,
        List<String> keys
) {

}
