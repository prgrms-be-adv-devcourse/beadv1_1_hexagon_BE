package com.example.profileservice.profile.model.dto.response;

import com.example.profileservice.resume.model.dto.response.ResumeDetailResponse;
import com.example.profileservice.selfPromotion.model.dto.response.SelfPromotionResponse;
import com.example.profileservice.tag.model.dto.response.TagResponse;

import java.util.List;

public record ProfileReadResponse(
    ResumeDetailResponse resume,
    SelfPromotionResponse selfPromotion,
    List<TagResponse> tags
) {
}
