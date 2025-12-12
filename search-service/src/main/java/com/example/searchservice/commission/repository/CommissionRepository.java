package com.example.searchservice.commission.repository;

import com.example.searchservice.commission.entity.CommissionDocumentEntity;
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
