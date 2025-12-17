package com.example.recommendationservice.controller;

import com.example.recommendationservice.common.exception.RecommendationException;
import com.example.recommendationservice.controller.dto.response.FreelancerRecommendListResponse;
import com.example.recommendationservice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.web.bind.annotation.*;

import static com.example.recommendationservice.common.exception.status.ResponseStatusCode.INVALID_RECOMMENDATION_COUNT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    // 프리랜서 추천 API
    @GetMapping("/commissions/{commission-code}/freelancers")
    public ResponseDto<FreelancerRecommendListResponse> recommendFreelancers(
        @PathVariable("commission-code") String commissionCode,
        @RequestParam(defaultValue = "3") int count
    ) {
        if (count < 1) {
            throw new RecommendationException(INVALID_RECOMMENDATION_COUNT);
        }

        return ResponseDto.success(
            recommendationService.recommendFreelancers(commissionCode, count)
        );
    }

}
