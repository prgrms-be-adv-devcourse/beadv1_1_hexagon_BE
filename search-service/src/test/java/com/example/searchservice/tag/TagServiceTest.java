package com.example.searchservice.tag;

import com.example.searchservice.tag.dto.TagResponseDto;
import com.example.searchservice.tag.service.TagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class TagServiceTest {

    @Autowired
    private TagService tagService;

    @Test
    @DisplayName("한글로 자동완성")
    void testSuggestByKoreanAlias() {
        List<TagResponseDto> results = tagService.getSuggestions("자", 5);

        System.out.println("=== Suggest 결과 (입력: 자) ===");
        for (TagResponseDto dto : results) {
            System.out.println("code: " + dto.code() + ", skill: " + dto.skill());
        }
    }

    @Test
    @DisplayName("영어로 자동완성")
    void testSuggestByEnglishPrefix() {
        List<TagResponseDto> results = tagService.getSuggestions("spr", 5);

        System.out.println("=== Suggest 결과 (입력: spr) ===");
        for (TagResponseDto dto : results) {
            System.out.println("code: " + dto.code() + ", skill: " + dto.skill());
        }
    }

    @Test
    @DisplayName("Exact Match(정확히 일치하는 텍스트를 입력하였을 때) 테스트")
    void testSuggestByExactMatch() {
        List<TagResponseDto> results = tagService.getSuggestions("React", 5);

        System.out.println("=== Suggest 결과 (입력: React) ===");
        for (TagResponseDto dto : results) {
            System.out.println("code: " + dto.code() + ", skill: " + dto.skill());
        }
    }

    @Test
    @DisplayName("태그 code로 태그명을 찾는 테스트")
    void findTagsByCodesTest() {
        List<String> codes = List.of("3f7c6e8e-9b41-4c4f-9f4c-5f4b3b4e1a01", "b1a0c6c4-3c7a-4d2a-9f2c-0c7f8a4c1b02");
        List<String> skills = tagService.findTagsByCodes(codes);
        for (String skill : skills) {
            System.out.println("skill = " + skill);
        }
    }
}