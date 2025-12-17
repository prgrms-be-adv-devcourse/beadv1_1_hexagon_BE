package com.example.recommendationservice.client.dto.output;

import java.util.List;
import java.util.stream.Collectors;

public record ProfileReadOutput(
    ResumeReadOutput resume,
    SelfPromotionReadOutput selfPromotion,
    List<TagReadOutput> tags
) {
    // 임베딩을 위한 텍스트 변환
    public String toEmbeddingText() {
        StringBuilder sb = new StringBuilder();

        // resume
        if (resume != null) {
            sb.append("이력서 제목: ").append(nullToEmpty(resume.title())).append("\n");
            sb.append("이력서 내용: ").append(nullToEmpty(resume.body())).append("\n");

            // experiences
            if (resume.experiences() != null && !resume.experiences().isEmpty()) {
                for (ExperienceReadOutput experience : resume.experiences()) {
                    if (experience != null) {
                        sb.append("경력/경험 제목: ").append(nullToEmpty(experience.title())).append("\n");
                        sb.append("회사/기관: ").append(nullToEmpty(experience.organization())).append("\n");
                        sb.append("경력/경험 설명: ").append(nullToEmpty(experience.description())).append("\n");
                    }
                }
            }
        }

        // self-promotion
        if (selfPromotion != null) {
            sb.append("자기소개 제목: ").append(nullToEmpty(selfPromotion.title())).append("\n");
            sb.append("자기소개 내용: ").append(nullToEmpty(selfPromotion.content())).append("\n");
        }

        // skill tags
        if (tags != null && !tags.isEmpty()) {
            String skillTagsText = tags.stream()
                .filter(tag -> tag != null && tag.skill() != null)
                .map(TagReadOutput::skill)
                .collect(Collectors.joining(", "));

            if (!skillTagsText.isBlank()) {
                sb.append("보유한 기술: ").append(skillTagsText).append("\n");
            }
        }

        return sb.toString().trim();
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }
}
