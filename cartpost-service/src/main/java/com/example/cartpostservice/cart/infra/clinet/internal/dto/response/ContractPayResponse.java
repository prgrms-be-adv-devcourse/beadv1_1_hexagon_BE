package com.example.cartpostservice.cart.infra.clinet.internal.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ContractPayResponse(
        List<String> success,
        List<String> fail
) {

}