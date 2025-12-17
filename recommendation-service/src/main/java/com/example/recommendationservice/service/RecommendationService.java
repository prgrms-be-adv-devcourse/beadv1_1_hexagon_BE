package com.example.recommendationservice.service;

import com.example.recommendationservice.client.CommissionServiceClient;
import com.example.recommendationservice.client.ContractServiceClient;
import com.example.recommendationservice.client.ProfileServiceClient;
import com.example.recommendationservice.client.dto.output.CommissionReadOutput;
import com.example.recommendationservice.client.dto.output.ProfileReadOutput;
import com.example.recommendationservice.controller.dto.response.FreelancerRecommendListResponse;
import com.example.recommendationservice.controller.dto.response.FreelancerRecommendReasonResponse;
import com.example.recommendationservice.mapper.RecommendationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final VectorStore vectorStore;
    private final CommissionServiceClient commissionServiceClient;
    private final ContractServiceClient contractServiceClient;
    private final ChatClient chatClient;
    private final ProfileServiceClient profileServiceClient;

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

    /**
     * 프리랜서 추천 사유를 생성합니다.
     * @param commissionCode 의뢰글 코드
     * @param freelancerCode 프리랜서 코드
     * @return 프리랜서 추천 사유
     */
    public FreelancerRecommendReasonResponse generateRecommendationReason(String commissionCode, String freelancerCode) {
        // 의뢰글 조회
        CommissionReadOutput commission = commissionServiceClient
            .getCommission(commissionCode)
            .data();

        // 프리랜서 프로필 조회
        Optional<Document> optionalDocument = fetchDocumentByFreelancerCode(freelancerCode);
        String profileText;

        if (optionalDocument.isPresent() && optionalDocument.get().getText() != null) { // vector search 성공
            profileText = optionalDocument.get().getText();
        } else { // vector search 실패
            ProfileReadOutput profile = profileServiceClient // feign client 시도
                .getFreelancerProfile(freelancerCode)
                .data();

            profileText = (profile != null) ? profile.toEmbeddingText() : "정보 없음";
        }

        // 프롬프트 구성
        String systemText = """
            당신은 전문적인 IT 프로젝트 매칭 매니저입니다.
            
            제공된 '의뢰글'과 프리랜서의 '프로필'을 분석하여,
            이 프리랜서가 해당 프로젝트에 적합한 이유를 설명하세요.
            
            [요구 사항]
            1. 3줄 이내로 간결하고 설득력 있게 작성하세요.
            2. 프리랜서의 보유 기술, 관련 경력 및 경험을 의뢰글과 연결지어 설명하세요.
            3. 보유 기술, 관련 경력 및 경험, 의뢰 프로젝트 적합성 순서대로 작성하세요.
            4. 불필요한 일반적인 칭찬은 생략하고 구체적인 기술적 근거를 제시하세요.
            """;

        String userText = """
            [의뢰글]
            제목: {commissionTitle}
            내용: {commissionContent}
            
            [프리랜서 프로필]
            {profile}
            """;

        // AI chat client 호출
        String reason = chatClient.prompt()
            .system(system -> system.text(systemText))
            .user(user -> user.text(userText)
                .param("commissionTitle", commission.title())
                .param("commissionContent", commission.content())
                .param("profile", profileText))
            .call()
            .content();

        log.info("[recommendation] recommendation reason generated: freelancer={}, commission={}", freelancerCode, commissionCode);

        // 응답 반환
        return new FreelancerRecommendReasonResponse(
            commissionCode,
            freelancerCode,
            reason
        );
    }

    private Optional<Document> fetchDocumentByFreelancerCode(String freelancerCode) {
        // 특정 freelancer code를 가진 document 검색
        List<Document> documents = vectorStore.similaritySearch(
            SearchRequest.builder()
                .query("")
                .topK(1) // freelancer code == unique
                .filterExpression(
                    new FilterExpressionBuilder()
                        .in("freelancerCode", freelancerCode)
                        .build()
                )
                .build()
        );

        return documents.stream().findFirst();
    }

}
