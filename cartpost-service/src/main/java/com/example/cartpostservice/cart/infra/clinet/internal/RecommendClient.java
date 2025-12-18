package com.example.cartpostservice.cart.infra.clinet.internal;

import com.example.cartpostservice.cart.infra.clinet.internal.dto.response.FreelancerRecommendListResponse;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "recommendation-service")
public interface RecommendClient {

    @GetMapping("/internal/recommendations/commissions/{commission-code}/freelancers")
    ResponseDto<FreelancerRecommendListResponse> recommendFreelancers(
            @PathVariable("commission-code") String commissionCode,
            @RequestParam(defaultValue = "3") int count
    );
}
