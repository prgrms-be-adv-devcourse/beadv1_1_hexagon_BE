package com.example.profileservice.common.model.vo.util;

import java.util.List;

public record StoreKeysRequest(
        String code,
        List<String> keys
) {

}
