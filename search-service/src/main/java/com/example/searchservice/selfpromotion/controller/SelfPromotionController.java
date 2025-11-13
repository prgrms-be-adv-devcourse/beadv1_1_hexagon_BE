package com.example.searchservice.selfpromotion.controller;

import com.example.searchservice.common.vo.SearchScope;
import com.example.searchservice.selfpromotion.controller.swagger.SelfPromotionControllerSwagger;
import com.example.searchservice.selfpromotion.dto.SelfPromotionDto;
import com.example.searchservice.selfpromotion.service.SelfPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search/self-promotions")
public class SelfPromotionController implements SelfPromotionControllerSwagger {

    private final SelfPromotionService selfPromotionService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<SelfPromotionDto>> search(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "all") String scope,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        SearchScope searchScope = SearchScope.from(scope);

        Page<SelfPromotionDto> result;

        // 1) 검색어가 비어있으면 -> 전체 조회 (scope 상관없이 최신순)
        if (q == null || q.isBlank()) {
            result = selfPromotionService.searchAll(page, size);
        } else {
            // 2) 검색어가 있으면 scope 에 따라 분기
            switch (searchScope) {
                case TITLE -> result = selfPromotionService.searchByTitle(q, page, size);
                case CONTENT -> result = selfPromotionService.searchByContent(q, page, size);
                default -> result = selfPromotionService.searchByTitleAndContent(q, page, size);
            }
        }

        // 나중에 공통 응답 DTO(BaseResponse 같은)로 감싸고 싶으면 여기서 감싸면 됨
        return ResponseEntity.ok(result);
    }

    @GetMapping("/suggest")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> suggest(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int size) {
        return null;
    }
}