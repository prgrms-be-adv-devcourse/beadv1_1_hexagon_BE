package com.example.searchservice.commission.repository;

import com.example.searchservice.commission.dto.CommissionResponseDto;
import com.example.searchservice.commission.entity.CommissionDocumentEntity;
import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CommissionRepository extends ElasticsearchRepository<CommissionDocumentEntity, String> {

    @Query("""
            {
              "multi_match": {
                "query": "#{#query}",
                "fields": ["title", "content"],
                "minimum_should_match": "2<75%",
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
                  "minimum_should_match": "2<75%",
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
                  "minimum_should_match": "2<75%",
                  "fuzziness": "1"
                }
              }
            }
            """)
    Page<CommissionDocumentEntity> searchContent(String query, Pageable pageable);

    @Query("""
            {
              "multi_match": {
                  "query": "#{#query}",
                  "type": "bool_prefix",
                  "fields": [
                    "title.completion",
                    "title.completion._2gram",
                    "title.completion._3gram"
                  ]
                }
            }
            """)
    SearchHits<CommissionDocumentEntity> autoComplete(String query);
}
