package com.example.searchservice.selfpromotion.repository;

import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SelfPromotionRepository extends ElasticsearchRepository<SelfPromotionDocumentEntity, String> {

    Page<SelfPromotionDocumentEntity> findByTitleContainingOrContentContaining(
            String title,
            String content,
            Pageable pageable
    );

    Page<SelfPromotionDocumentEntity> findByTitleContaining(
            String title,
            Pageable pageable
    );

    Page<SelfPromotionDocumentEntity> findByContentContaining(
            String content,
            Pageable pageable
    );
}
