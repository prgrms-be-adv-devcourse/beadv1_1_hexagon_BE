package com.example.searchservice.selfpromotion.service;

import com.example.searchservice.common.vo.SearchScope;
import com.example.searchservice.selfpromotion.dto.SelfPromotionDto;
import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;
import com.example.searchservice.selfpromotion.repository.SelfPromotionRepository;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SelfPromotionServiceImpl implements SelfPromotionService {

    private final SelfPromotionRepository selfPromotionRepository;

    @Override
    public Page<SelfPromotionDto> search(String q, SearchScope scope, int page, int size) {

        if (q == null || q.isBlank()) {
            PageRequest sortedByUpdatedAt = PageRequest.of(
                    page,
                    size,
                    Sort.by(Sort.Direction.DESC, "updatedAt")
            );

            return selfPromotionRepository.findAll(sortedByUpdatedAt)
                    .map(SelfPromotionDto::from);
        }

        PageRequest pageable = PageRequest.of(page, size);

        return switch (scope) {
            case ALL       -> selfPromotionRepository.searchAllFields(q, pageable).map(SelfPromotionDto::from);
            case TITLE     -> selfPromotionRepository.searchTitle(q, pageable).map(SelfPromotionDto::from);
            case CONTENT   -> selfPromotionRepository.searchContent(q, pageable).map(SelfPromotionDto::from);
        };
    }
}
