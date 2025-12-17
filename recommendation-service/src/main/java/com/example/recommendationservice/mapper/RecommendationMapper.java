package com.example.recommendationservice.mapper;

import com.example.recommendationservice.controller.dto.response.FreelancerRecommendListResponse;
import com.example.recommendationservice.controller.dto.response.FreelancerRecommendResponse;
import org.springframework.ai.document.Document;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class RecommendationMapper {

    private RecommendationMapper() {}

    public static FreelancerRecommendListResponse toEmptyResponse() {
        return new FreelancerRecommendListResponse(Collections.emptyList());
    }

    public static FreelancerRecommendListResponse toReadResponse(List<Document> documents) {
        List<FreelancerRecommendResponse> recommendations = documents.stream()
            .map(RecommendationMapper::toFreelancerRecommendResponse)
            .toList();

        return new FreelancerRecommendListResponse(recommendations);
    }

    private static FreelancerRecommendResponse toFreelancerRecommendResponse(Document document) {
        String freelancerCode = extractFreelancerCode(document);
        Double similarityScore = Optional.ofNullable(document.getScore()).orElse(0.0);

        return new FreelancerRecommendResponse(freelancerCode, similarityScore);
    }

    private static String extractFreelancerCode(Document document) {
        Object freelancerCodeObject = document.getMetadata().getOrDefault("freelancerCode", "");

        if (freelancerCodeObject instanceof String s) {
            return s;
        } else {
            return "";
        }
    }

}
