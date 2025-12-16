package com.example.cartpostservice.commissions.service.usecase.result;

import java.util.List;

public record TagServiceResult(
        String commissionCode,

        List<String> tagCodes
) {

}
