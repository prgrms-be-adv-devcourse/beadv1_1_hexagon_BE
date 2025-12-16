package com.example.searchservice.tag.service;

import com.example.searchservice.tag.dto.TagResponseDto;
import com.example.searchservice.tag.entity.TagDocumentEntity;
import java.util.List;

public interface TagService {
    public List<TagResponseDto> getSuggestions(String prefix, int size);

    public void saveAll(List<TagDocumentEntity> tags);
}

