package com.example.searchservice.selfpromotion.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.AnalyzeRequest;
import co.elastic.clients.elasticsearch.indices.AnalyzeResponse;
import co.elastic.clients.elasticsearch.indices.analyze.AnalyzeToken;
import com.example.searchservice.common.vo.SearchScope;
import com.example.searchservice.selfpromotion.dto.SelfPromotionResponseDto;
import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;
import com.example.searchservice.selfpromotion.repository.SelfPromotionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SelfPromotionServiceImpl implements SelfPromotionService {

    private final SelfPromotionRepository selfPromotionRepository;
    private final ElasticsearchClient esClient;

    @Override
    public Page<SelfPromotionResponseDto> search(String query, SearchScope scope, int page, int size) {

        if (query == null || query.isBlank()) {
            PageRequest sortedByUpdatedAt = PageRequest.of(
                    page,
                    size,
                    Sort.by(Sort.Direction.DESC, "updatedAt")
            );

            return selfPromotionRepository.findAll(sortedByUpdatedAt)
                    .map(SelfPromotionResponseDto::from);
        }

        PageRequest pageable = PageRequest.of(page, size);

        return switch (scope) {
            case all       -> selfPromotionRepository.searchAll(query, pageable).map(SelfPromotionResponseDto::from);
            case title     -> selfPromotionRepository.searchTitle(query, pageable).map(SelfPromotionResponseDto::from);
            case content   -> selfPromotionRepository.searchContent(query, pageable).map(SelfPromotionResponseDto::from);
        };
    }

    @Override
    public List<String> getSuggestions(String query, int size) {
        SearchHits<SelfPromotionDocumentEntity> searchHits = selfPromotionRepository.autoComplete(query);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .map(SelfPromotionDocumentEntity::getTitle)
                .map(this::extractNounsWithEs)
                .distinct()
                .limit(size)
                .toList();
    }

    @Override
    public void saveAll(List<SelfPromotionDocumentEntity> selfPromotions) {
        selfPromotionRepository.saveAll(selfPromotions);
    }

    @Override
    public void save(SelfPromotionDocumentEntity selfPromotion) {
        selfPromotionRepository.save(selfPromotion);
    }

    @Override
    public void update(SelfPromotionDocumentEntity selfPromotion) {
        selfPromotionRepository.save(selfPromotion);
    }

    @Override
    public void delete(String code) {
        selfPromotionRepository.deleteById(code);
    }

    // ES에 _analyze 요청 보내 "korean_noun_analyzer" 통해 명사만 남기는 로직
    private String extractNounsWithEs(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        try {
            AnalyzeRequest req = AnalyzeRequest.of(a -> a
                    .index("commissions")            // 인덱스 이름
                    .analyzer("korean_noun_analyzer") // 네가 정의한 analyzer
                    .text(text)
            );

            AnalyzeResponse response = esClient
                    .indices()
                    .analyze(req);

            List<String> tokens = response.tokens()
                    .stream()
                    .map(AnalyzeToken::token)
                    .toList();

            return String.join(" ", tokens);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
