package com.example.searchservice.commission.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.AnalyzeRequest;
import co.elastic.clients.elasticsearch.indices.AnalyzeResponse;
import co.elastic.clients.elasticsearch.indices.analyze.AnalyzeToken;
import com.example.searchservice.commission.dto.CommissionResponseDto;
import com.example.searchservice.commission.entity.CommissionDocumentEntity;
import com.example.searchservice.commission.repository.CommissionRepository;
import com.example.searchservice.common.vo.SearchScope;
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
public class CommissionServiceImpl implements CommissionService {

    private final CommissionRepository commissionRepository;
    private final ElasticsearchClient esClient;

    @Override
    public Page<CommissionResponseDto> search(String query, SearchScope scope, int page, int size) {

        if (query == null || query.isBlank()) {
            PageRequest sortedByUpdatedAt = PageRequest.of(
                    page,
                    size,
                    Sort.by(Sort.Direction.DESC, "updatedAt")
            );

            return commissionRepository.findAll(sortedByUpdatedAt)
                    .map(CommissionResponseDto::from);
        }

        PageRequest pageable = PageRequest.of(page, size);

        return switch (scope) {
            case all       -> commissionRepository.searchAll(query, pageable).map(CommissionResponseDto::from);
            case title     -> commissionRepository.searchTitle(query, pageable).map(CommissionResponseDto::from);
            case content   -> commissionRepository.searchContent(query, pageable).map(CommissionResponseDto::from);
        };
    }

    @Override
    public List<String> getSuggestions(String query, int size) {
        SearchHits<CommissionDocumentEntity> searchHits = commissionRepository.autoComplete(query);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .map(CommissionDocumentEntity::getTitle)
                .map(this::extractNounsWithEs)
                .distinct()
                .limit(size)
                .toList();
    }

    @Override
    public void saveAll(List<CommissionDocumentEntity> commissions) {
        commissionRepository.saveAll(commissions);
    }

    @Override
    public void save(CommissionDocumentEntity commission) {
        commissionRepository.save(commission);
    }

    @Override
    public void update(CommissionDocumentEntity commission) {
        commissionRepository.save(commission);
    }

    @Override
    public void delete(String code) {
        commissionRepository.deleteById(code);
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
