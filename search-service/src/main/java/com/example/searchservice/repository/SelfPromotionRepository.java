package com.example.searchservice.repository;

import com.example.searchservice.entity.SelfPromotionDocumentEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SelfPromotionRepository extends ElasticsearchRepository<SelfPromotionDocumentEntity, String> {

}
