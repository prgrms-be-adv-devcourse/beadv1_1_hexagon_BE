package com.example.searchservice.selfpromotion.repository;

import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SelfPromotionRepository extends ElasticsearchRepository<SelfPromotionDocumentEntity, String> {

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
    SearchHits<SelfPromotionDocumentEntity> autoComplete(String query);
}
