package com.example.searchservice.selfpromotion.service;

import com.example.searchservice.common.vo.SearchScope;
import com.example.searchservice.selfpromotion.dto.SelfPromotionResponseDto;
import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;
import java.util.List;
import org.springframework.data.domain.Page;

public interface SelfPromotionService {

    public Page<SelfPromotionResponseDto> search(String query, SearchScope scope, int page, int size);

    public void saveAll(List<SelfPromotionDocumentEntity> selfPromotions);

    public void save(SelfPromotionDocumentEntity selfPromotion);

    public void update(SelfPromotionDocumentEntity selfPromotion);

    public void delete(String code);
}
