package com.example.searchservice.tag.service;

import com.example.searchservice.tag.dto.TagResponseDto;
import java.util.List;

public interface TagService {
    public List<TagResponseDto> getSuggestions(String prefix, int size);
}
