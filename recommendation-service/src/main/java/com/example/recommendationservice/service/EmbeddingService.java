package com.example.recommendationservice.service;

import com.example.recommendationservice.client.ProfileServiceClient;
import com.example.recommendationservice.client.dto.output.ProfileReadOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final VectorStore vectorStore;
    private final ProfileServiceClient profileServiceClient;

    /**
     * 프리랜서 프로필 생성/수정 이벤트 발생 시 프로필을 벡터 스토어에 임베딩합니다.
     * @param freelancerCode 프리랜서 코드
     */
    public void embedFreelancerProfile(String freelancerCode) {
        // 프리랜서 프로필 조회
        ProfileReadOutput freelancerProfile = profileServiceClient
            .getFreelancerProfile(freelancerCode)
            .data();

        // metadata 생성
        // 특정 의뢰글에 지원한 프리랜서 필터링 시 사용
        Map<String, Object> metadata = Map.of("freelancerCode", freelancerCode);

        // document 생성
        Document document = new Document(
            freelancerCode, // id // 이미 저장된 document와 id가 같으면 덮어쓰기
            freelancerProfile.toEmbeddingText(), // text
            metadata // metadata
        );

        // vector DB 저장
        vectorStore.add(List.of(document));

        log.info("[recommendation] freelancer profile embedded: freelancer={}", freelancerCode);
    }

}
