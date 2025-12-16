package com.example.memberservice.common.client.dto.request.s3;

import java.util.List;

public record StoreKeysRequest(
        String code,
        List<String> keys
) {

}
