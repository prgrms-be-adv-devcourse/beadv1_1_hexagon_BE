package com.example.searchservice.tag;

import com.example.searchservice.tag.dto.TagResponseDto;
import com.example.searchservice.tag.service.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class TagServiceTest {

    @Autowired
    private TagService tagService;

    @Test
    void testSuggestByKoreanAlias() {
        List<TagResponseDto> results = tagService.getSuggestions("자", 5);

        System.out.println("=== Suggest 결과 (입력: 자) ===");
        for (TagResponseDto dto : results) {
            System.out.println("code: " + dto.code() + ", skill: " + dto.skill());
        }
    }

    @Test
    void testSuggestByEnglishPrefix() {
        List<TagResponseDto> results = tagService.getSuggestions("spr", 5);

        System.out.println("=== Suggest 결과 (입력: spr) ===");
        for (TagResponseDto dto : results) {
            System.out.println("code: " + dto.code() + ", skill: " + dto.skill());
        }
    }

    @Test
    void testSuggestByExactMatch() {
        List<TagResponseDto> results = tagService.getSuggestions("React", 5);

        System.out.println("=== Suggest 결과 (입력: React) ===");
        for (TagResponseDto dto : results) {
            System.out.println("code: " + dto.code() + ", skill: " + dto.skill());
        }
    }
}