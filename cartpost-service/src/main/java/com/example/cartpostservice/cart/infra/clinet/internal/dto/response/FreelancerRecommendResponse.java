package com.example.cartpostservice.cart.infra.clinet.internal.dto.response;

public record FreelancerRecommendResponse(
        String freelancerCode,
        Double similarityScore
) {

}
