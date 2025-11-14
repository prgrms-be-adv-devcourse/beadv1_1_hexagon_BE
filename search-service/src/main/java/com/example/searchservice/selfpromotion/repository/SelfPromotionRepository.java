package com.example.searchservice.selfpromotion.repository;

import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SelfPromotionRepository extends ElasticsearchRepository<SelfPromotionDocumentEntity, String> {

    @Query("""
            {
              "multi_match": {
                "query": "#{#q}",
                "fields": ["title", "content"],
                "operator": "or",
                "fuzziness": "1"
              }
            }
            """)
    Page<SelfPromotionDocumentEntity> searchAllFields(String q, Pageable pageable);

    @Query("""
            {
              "match": {
                "title": {
                  "query": "#{#q}",
                  "fuzziness": "1"
                }
              }
            }
            """)
    Page<SelfPromotionDocumentEntity> searchTitle(String q, Pageable pageable);

    @Query("""
            {
              "match": {
                "content": {
                  "query": "#{#q}",
                  "fuzziness": "1"
                }
              }
            }
            """)
    Page<SelfPromotionDocumentEntity> searchContent(String q, Pageable pageable);
}
