package com.example.contractservice.contract.service.dto.request;

import com.example.contractservice.contract.domain.Contract;
import com.example.contractservice.contract.entity.ContractEntity;

public record ContractPayProcessRequest(
        String xCode,
        Contract contract,
        ContractEntity contractEntity
) {

}
