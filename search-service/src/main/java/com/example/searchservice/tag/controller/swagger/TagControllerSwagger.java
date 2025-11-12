package com.example.searchservice.tag.controller.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Tag Suggest API", description = "Tag 검색어 추천 API")
public interface TagControllerSwagger {

    @Operation(summary = "Tag 추천 검색어", description = "입력한 접두어(prefix)를 기반으로 추천 검색어를 반환합니다.")
    @Parameters({
            @Parameter(name = "q", description = "검색어 접두어", required = true),
            @Parameter(name = "size", description = "보여줄 추천 검색어 개수", required = false)
    })
    ResponseEntity<Object> suggest(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int size
    );
}
