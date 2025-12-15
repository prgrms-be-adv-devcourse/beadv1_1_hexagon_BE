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

    @Test
    void findTagsByCodesTest() {
        List<String> codes = List.of("3f7c6e8e-9b41-4c4f-9f4c-5f4b3b4e1a01", "b1a0c6c4-3c7a-4d2a-9f2c-0c7f8a4c1b02");
        List<String> skills = tagService.findTagsByCode(codes);
        for (String skill : skills) {
            System.out.println("skill = " + skill);
        }
    }
}