package com.example.searchservice.selfpromotion.service;

import com.example.searchservice.selfpromotion.dto.SelfPromotionDto;
import com.example.searchservice.selfpromotion.repository.SelfPromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SelfPromotionServiceImpl implements SelfPromotionService {

    private final SelfPromotionRepository repository;

    @Override
    public Page<SelfPromotionDto> searchAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "updatedAt")   // 최신순
        );

        return repository.findAll(pageRequest)
                .map(SelfPromotionDto::from);
    }

    @Override
    public Page<SelfPromotionDto> searchByTitleAndContent(String q, int page, int size) {
        return repository.findByTitleContainingOrContentContaining(q, q, PageRequest.of(page, size))
                .map(SelfPromotionDto::from);
    }

    @Override
    public Page<SelfPromotionDto> searchByTitle(String q, int page, int size) {
        return repository.findByTitleContaining(q, PageRequest.of(page, size))
                .map(SelfPromotionDto::from);
    }

    @Override
    public Page<SelfPromotionDto> searchByContent(String q, int page, int size) {
        return repository.findByContentContaining(q, PageRequest.of(page, size))
                .map(SelfPromotionDto::from);
    }
}
