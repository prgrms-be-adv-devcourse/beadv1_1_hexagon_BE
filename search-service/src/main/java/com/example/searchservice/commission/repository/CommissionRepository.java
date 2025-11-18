package com.example.searchservice.commission.repository;

import com.example.searchservice.commission.dto.CommissionResponseDto;
import com.example.searchservice.commission.entity.CommissionDocumentEntity;
import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CommissionRepository extends ElasticsearchRepository<CommissionDocumentEntity, String> {

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
    Page<CommissionDocumentEntity> searchAll(String query, Pageable pageable);

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
    Page<CommissionDocumentEntity> searchTitle(String query, Pageable pageable);

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
    Page<CommissionDocumentEntity> searchContent(String query, Pageable pageable);
}
