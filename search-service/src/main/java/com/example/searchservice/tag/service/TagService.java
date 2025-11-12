package com.example.searchservice.tag.service;

import com.example.searchservice.tag.dto.TagDto;
import java.util.List;

public interface TagService {
    public List<TagDto> getSuggestions(String prefix, int size);
}
