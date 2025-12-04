package com.example.cartpostservice.commissions.controller.dto.request.internal;

import java.util.List;

public record FilesRequestDto(
        String code,
        List<String> keys
) {

}
