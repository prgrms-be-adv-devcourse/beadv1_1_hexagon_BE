package com.example.memberservice.common.client.dto.response;

public record ContractStateResponse(
    boolean isClient,
    boolean isFreelancer
) {

}
