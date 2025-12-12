package com.example.contractservice.contract.controller.dto.response;

public record MemberRoleStatusResponse(
        boolean isClient,
        boolean isFreelancer
) {

}
