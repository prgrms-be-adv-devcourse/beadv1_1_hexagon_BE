package com.example.recommendationservice.controller.dto.response;

public record FreelancerRecommendResponse(
    String freelancerCode,
    Double similarityScore
) {
}
