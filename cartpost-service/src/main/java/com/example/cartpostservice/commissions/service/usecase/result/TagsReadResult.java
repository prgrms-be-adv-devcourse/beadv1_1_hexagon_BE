package com.example.cartpostservice.commissions.service.usecase.result;

import java.util.List;

public record TagsReadResult(
        String commissionCode,

        List<String> tagCodes
) {

}
