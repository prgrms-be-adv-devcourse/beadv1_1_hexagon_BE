package com.example.contractservice.contract.controller.dto.request;

public record ContractCancelRequest(
        String xCode,
        String contractCode
) {

}
