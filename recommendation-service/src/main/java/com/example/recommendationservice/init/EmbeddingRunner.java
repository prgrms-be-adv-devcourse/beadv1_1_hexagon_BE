package com.example.recommendationservice.init;

import com.example.recommendationservice.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmbeddingRunner implements ApplicationRunner {

    private final EmbeddingService embeddingService;

    @Value("#{'${mock.freelancer.code}'.split(',')}")
    private List<String> freelancerCodes;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (String freelancerCode : freelancerCodes) {
            embeddingService.embedFreelancerProfile(freelancerCode);
        }
    }

}
