package org.hexagon.s3service.dto;

import java.util.List;

public record StoreKeysRequest(
        String code,
        List<String> keys
) {

}
