package com.example.searchservice.tag.controller;

import com.example.searchservice.common.response.BaseResponse;
import com.example.searchservice.tag.controller.swagger.TagControllerSwagger;
import com.example.searchservice.tag.dto.TagDto;
import com.example.searchservice.tag.service.TagService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search/tags")
@RequiredArgsConstructor
public class TagController implements TagControllerSwagger {

    private final TagService tagService;

    @GetMapping("/suggest")
    public BaseResponse<List<TagDto>> suggest(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<TagDto> suggestions = tagService.getSuggestions(q, size);
        return new BaseResponse<>(200, "자동완성 목록 조회 성공", suggestions);
    }
}
