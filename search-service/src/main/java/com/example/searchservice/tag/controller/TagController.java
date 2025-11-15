package com.example.searchservice.tag.controller;

import com.example.searchservice.common.response.BaseResponse;
import com.example.searchservice.tag.controller.swagger.TagControllerSwagger;
import com.example.searchservice.tag.dto.TagResponseDto;
import com.example.searchservice.tag.service.TagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search/tags")
@RequiredArgsConstructor
public class TagController implements TagControllerSwagger {

    private final TagService tagService;

    @GetMapping("/suggest")
    public BaseResponse<List<TagResponseDto>> suggest(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<TagResponseDto> suggestions = tagService.getSuggestions(query, size);
        return new BaseResponse<>(0,200, "요청이 성공하였습니다.", suggestions);
    }
}
