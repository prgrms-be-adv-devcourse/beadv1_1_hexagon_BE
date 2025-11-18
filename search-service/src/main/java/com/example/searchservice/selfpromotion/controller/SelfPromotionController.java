package com.example.searchservice.selfpromotion.controller;

import com.example.searchservice.common.response.BaseResponse;
import com.example.searchservice.common.vo.SearchScope;
import com.example.searchservice.selfpromotion.controller.swagger.SelfPromotionControllerSwagger;
import com.example.searchservice.selfpromotion.dto.SelfPromotionResponseDto;
import com.example.searchservice.selfpromotion.service.SelfPromotionService;
import java.util.List;
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
    public BaseResponse<Page<SelfPromotionResponseDto>> search(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "all") SearchScope scope,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<SelfPromotionResponseDto> result;

        result = selfPromotionService.search(query, scope, page, size);

        return BaseResponse.ok(result);
    }

    @GetMapping("/suggest")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<List<String>> suggest(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int size) {

        return BaseResponse.ok(selfPromotionService.getSuggestions(query, size));
    }

}