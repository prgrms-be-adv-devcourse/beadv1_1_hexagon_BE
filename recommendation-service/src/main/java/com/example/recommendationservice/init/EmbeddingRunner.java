package com.example.recommendationservice.init;

import com.example.recommendationservice.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmbeddingRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingService embeddingService;

    @Override
    public void run(ApplicationArguments args) {
        List<String> freelancerCodes = jdbcTemplate.queryForList(
            """
            SELECT code
            FROM members
            WHERE role IN ('FREELANCER', 'BOTH')
            """,
            String.class
        );

        for (String freelancerCode : freelancerCodes) {
            embeddingService.embedFreelancerProfile(freelancerCode);
        }
    }
}
