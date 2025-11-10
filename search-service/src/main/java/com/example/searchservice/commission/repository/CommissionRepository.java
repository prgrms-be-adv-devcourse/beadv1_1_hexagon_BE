package com.example.searchservice.commission.repository;

import com.example.searchservice.commission.entity.CommissionDocumentEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CommissionRepository extends ElasticsearchRepository<CommissionDocumentEntity, String> {

}
