package com.example.searchservice.selfpromotion.controller;

import com.example.searchservice.commission.common.SearchScope;
import com.example.searchservice.selfpromotion.controller.swagger.SelfPromotionSearchControllerSwagger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search/self-promotions")
public class SelfPromotionSearchController implements SelfPromotionSearchControllerSwagger {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> search(String q, SearchScope scope, int page, int size) {
        return null;
    }

    @GetMapping("/suggest")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> suggest(String q, int size) {
        return null;
    }
}
