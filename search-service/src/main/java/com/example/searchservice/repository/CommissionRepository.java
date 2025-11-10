package com.example.searchservice.repository;

import com.example.searchservice.entity.CommissionDocumentEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CommissionRepository extends ElasticsearchRepository<CommissionDocumentEntity, String> {

}
