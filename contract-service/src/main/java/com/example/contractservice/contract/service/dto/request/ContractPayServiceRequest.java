package com.example.contractservice.contract.service.dto.request;

import java.util.List;

public record ContractPayServiceRequest(
        String xCode,
        List<String> contractCodes
) {

}
