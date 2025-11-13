package com.example.searchservice.selfpromotion.service;

import com.example.searchservice.selfpromotion.dto.SelfPromotionDto;
import org.springframework.data.domain.Page;

public interface SelfPromotionService {

    Page<SelfPromotionDto> searchAll(int page, int size);

    Page<SelfPromotionDto> searchByTitleAndContent(String q, int page, int size);

    Page<SelfPromotionDto> searchByTitle(String q, int page, int size);

    Page<SelfPromotionDto> searchByContent(String q, int page, int size);
}
