package com.example.profileservice.profile.service;

import com.example.profileservice.profile.model.dto.response.ProfileReadResponse;
import com.example.profileservice.resume.model.dto.response.ResumeDetailResponse;
import com.example.profileservice.resume.service.ResumeService;
import com.example.profileservice.selfPromotion.model.dto.response.SelfPromotionResponse;
import com.example.profileservice.selfPromotion.service.SelfPromotionService;
import com.example.profileservice.tag.model.dto.response.TagResponse;
import com.example.profileservice.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ResumeService resumeService;
    private final SelfPromotionService selfPromotionService;
    private final TagService tagService;

    @Transactional(readOnly = true)
    public ProfileReadResponse findProfileByCode(String freelancerCode) {
        ResumeDetailResponse resume = resumeService.getResumeDetail(freelancerCode);
        SelfPromotionResponse selfPromotion = selfPromotionService.getMyPromotions(freelancerCode);
        List<TagResponse> tags = tagService.getMyTags(freelancerCode);

        return new ProfileReadResponse(
            resume,
            selfPromotion,
            tags
        );
    }

}
