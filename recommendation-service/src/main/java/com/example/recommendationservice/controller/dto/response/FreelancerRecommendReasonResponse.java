package com.example.recommendationservice.controller.dto.response;

public record FreelancerRecommendReasonResponse(
    String commissionCode,
    String freelancerCode,
    String reason
) {
}
