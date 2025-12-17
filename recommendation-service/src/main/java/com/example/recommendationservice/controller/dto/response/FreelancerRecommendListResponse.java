package com.example.recommendationservice.controller.dto.response;

import java.util.List;

public record FreelancerRecommendListResponse(
    List<FreelancerRecommendResponse> recommendations
) {
}
