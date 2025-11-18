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
                "query": "#{#query}",
                "fields": ["title", "content"],
                "operator": "or",
                "fuzziness": "1"
              }
            }
            """)
    Page<SelfPromotionDocumentEntity> searchAll(String query, Pageable pageable);

    @Query("""
            {
              "match": {
                "title": {
                  "query": "#{#query}",
                  "fuzziness": "1"
                }
              }
            }
            """)
    Page<SelfPromotionDocumentEntity> searchTitle(String query, Pageable pageable);

    @Query("""
            {
              "match": {
                "content": {
                  "query": "#{#query}",
                  "fuzziness": "1"
                }
              }
            }
            """)
    Page<SelfPromotionDocumentEntity> searchContent(String query, Pageable pageable);
}
