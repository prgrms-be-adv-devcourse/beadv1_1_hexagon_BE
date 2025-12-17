package com.example.recommendationservice.service;

import com.example.recommendationservice.client.CommissionServiceClient;
import com.example.recommendationservice.client.ContractServiceClient;
import com.example.recommendationservice.client.dto.output.CommissionReadOutput;
import com.example.recommendationservice.controller.dto.response.FreelancerRecommendListResponse;
import com.example.recommendationservice.mapper.RecommendationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final VectorStore vectorStore;
    private final CommissionServiceClient commissionServiceClient;
    private final ContractServiceClient contractServiceClient;

    /**
     * 의뢰글에 지원한 프리랜서 중 상위 {count}명을 추천합니다.
     * @param commissionCode 의뢰글 코드
     * @param count 추천할 프리랜서 최대 인원 수
     * @return 추천 프리랜서 목록
     */
    public FreelancerRecommendListResponse recommendFreelancers(String commissionCode, int count) {
        // 의뢰글 조회
        CommissionReadOutput commission = commissionServiceClient
            .getCommission(commissionCode)
            .data();

        // 의뢰글에 지원한 프리랜서 코드 목록 조회
        List<String> appliedFreelancerCodes = contractServiceClient
            .getAppliedFreelancerCodes(commissionCode).
            data();

        // 의뢰글에 지원한 프리랜서가 존재하지 않는 경우 조기 반환
        if (appliedFreelancerCodes.isEmpty()) {
            return RecommendationMapper.toEmptyResponse();
        }

        // vector search 수행
        // query: 의뢰글 제목 및 내용
        // metadata filtering: 지원한 프리랜서 코드 목록
        List<Document> documents = vectorStore.similaritySearch(
            SearchRequest.builder()
                .query(commission.toQueryText())
                .topK(count)
                .similarityThreshold(0.7)
                .filterExpression(
                    new FilterExpressionBuilder()
                        .in("freelancerCode", appliedFreelancerCodes.toArray())
                        .build()
                )
                .build()
        );

        log.info("[recommendation] vector search results: {} documents found", documents.size());

        // 응답 반환
        return RecommendationMapper.toReadResponse(documents);
    }

}
