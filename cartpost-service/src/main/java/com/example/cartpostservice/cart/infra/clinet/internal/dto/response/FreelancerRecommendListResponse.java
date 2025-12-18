package com.example.cartpostservice.cart.infra.clinet.internal.dto.response;

import java.util.List;

public record FreelancerRecommendListResponse(
        List<FreelancerRecommendResponse> recommendations
) {

}
