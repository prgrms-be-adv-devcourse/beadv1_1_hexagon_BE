package com.example.searchservice.selfpromotion.service;

import com.example.searchservice.common.vo.SearchScope;
import com.example.searchservice.selfpromotion.dto.SelfPromotionResponseDto;
import com.example.searchservice.selfpromotion.repository.SelfPromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SelfPromotionServiceImpl implements SelfPromotionService {

    private final SelfPromotionRepository selfPromotionRepository;

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
}
