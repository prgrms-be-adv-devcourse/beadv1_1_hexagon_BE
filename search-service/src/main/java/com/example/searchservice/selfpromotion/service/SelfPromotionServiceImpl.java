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
    public Page<SelfPromotionResponseDto> search(String q, SearchScope scope, int page, int size) {

        if (q == null || q.isBlank()) {
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
            case all       -> selfPromotionRepository.searchAllFields(q, pageable).map(SelfPromotionResponseDto::from);
            case title     -> selfPromotionRepository.searchTitle(q, pageable).map(SelfPromotionResponseDto::from);
            case content   -> selfPromotionRepository.searchContent(q, pageable).map(SelfPromotionResponseDto::from);
        };
    }
}
