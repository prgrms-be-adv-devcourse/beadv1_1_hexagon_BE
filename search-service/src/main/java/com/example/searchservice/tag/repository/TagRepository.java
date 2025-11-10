package com.example.searchservice.tag.repository;

import com.example.searchservice.tag.entity.TagDocumentEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TagRepository extends ElasticsearchRepository<TagDocumentEntity, String> {

}
