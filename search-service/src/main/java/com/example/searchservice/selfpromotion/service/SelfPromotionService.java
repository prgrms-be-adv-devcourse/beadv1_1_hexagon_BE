package com.example.searchservice.selfpromotion.service;

import com.example.searchservice.common.vo.SearchScope;
import com.example.searchservice.selfpromotion.dto.SelfPromotionResponseDto;
import org.springframework.data.domain.Page;

public interface SelfPromotionService {

    public Page<SelfPromotionResponseDto> search(String q, SearchScope scope, int page, int size);
}
