package com.example.recommendationservice.client.dto.output;

import java.util.List;

public record ResumeReadOutput(
    String title,
    String body,
    List<ExperienceReadOutput> experiences
) {
}
