package com.example.contractservice.contract.controller.dto.response;

import java.util.List;

public record ContractPayResponse(
        List<String> success,
        List<String> fail
) {

}
