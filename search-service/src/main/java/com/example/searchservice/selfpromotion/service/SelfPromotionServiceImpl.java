package com.example.searchservice.selfpromotion.service;

import com.example.searchservice.common.vo.SearchScope;
import com.example.searchservice.selfpromotion.dto.SelfPromotionResponseDto;
import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;
import com.example.searchservice.selfpromotion.repository.SelfPromotionRepository;
import java.util.List;
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
        selfPromotionRepository.findById(selfPromotion.getCode())
                .ifPresentOrElse(existing -> {
                    existing.setCode(selfPromotion.getCode());
                    existing.setTitle(selfPromotion.getTitle());
                    existing.setContent(selfPromotion.getContent());
                    existing.setMemberCode(selfPromotion.getMemberCode());
                    existing.setMemberNickname(selfPromotion.getMemberNickname());
                    existing.setUpdatedAt(selfPromotion.getUpdatedAt());

                    selfPromotionRepository.save(existing);
                }, () -> {
                    selfPromotionRepository.save(selfPromotion);
                });
    }

    @Override
    public void delete(String code) {
        selfPromotionRepository.deleteById(code);
    }
}
