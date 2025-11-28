package com.example.cartpostservice.cart.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public record ContractPayRequest(
        @Schema(description = "결제할 계약 코드", example = "a94472b1-be7d-4c5b-8342-94b5a175be9d")
        @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                message = "유효한 UUID 형식이어야 합니다.")
        List<String> codes
) {

}