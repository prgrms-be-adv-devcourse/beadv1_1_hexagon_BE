package com.example.searchservice.selfpromotion.controller.swagger;

import com.example.searchservice.selfpromotion.dto.SelfPromotionResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

@Tag(name = "Self Promotion Search API", description = "Self Promotion 검색 / 검색 키워드 추천 API")
public interface SelfPromotionControllerSwagger {

    @Operation(summary = "Self Promotion 검색", description = "검색어, 검색 범위를 기반으로 Self Promotion을 검색합니다.")
    @Parameters({
            @Parameter(name = "q", description = "검색어", required = false),
            @Parameter(name = "scope", description = "검색 범위 (all(default) | title | content)"),
            @Parameter(name = "page", description = "페이지 번호", required = false),
            @Parameter(name = "size", description = "페이지 크기", required = false)
    })
    ResponseEntity<Page<SelfPromotionResponseDto>> search(String q, String scope, int page, int size);

    @Operation(summary = "Self Promotion 추천 검색 키워드", description = "입력한 접두어(prefix)를 기반으로 추천 검색어 키워드를 반환합니다.")
    @Parameters({
            @Parameter(name = "q", description = "검색어 접두어", required = true),
            @Parameter(name = "size", description = "보여줄 추천 검색 키워드 개수", required = false)
    })
    ResponseEntity<Object> suggest(
            String q, int size
    );
}
