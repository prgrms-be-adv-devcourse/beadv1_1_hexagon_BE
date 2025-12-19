package com.example.recommendationservice.init;

import com.example.recommendationservice.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmbeddingRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingService embeddingService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            // 프리랜서 코드 조회
            @SuppressWarnings("SqlResolve")
            List<String> freelancerCodes = jdbcTemplate.queryForList(
                """
                SELECT code
                FROM members
                WHERE role IN ('FREELANCER', 'BOTH')
                """,
                String.class
            );

            // 임베딩
            for (String freelancerCode : freelancerCodes) {
                embeddingService.embedFreelancerProfile(freelancerCode);
            }
        } catch (Exception e) {
            log.error("[recommendation] initial embedding error", e);
        }
    }

}
