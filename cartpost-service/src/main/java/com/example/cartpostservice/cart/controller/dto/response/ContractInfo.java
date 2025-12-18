package com.example.cartpostservice.cart.controller.dto.response;

public record ContractInfo(
        String contractCode,

        String itemCode,

        String clientName,

        String freelancerName,

        String freelancerCode,

        String contractTitle

) {

}
