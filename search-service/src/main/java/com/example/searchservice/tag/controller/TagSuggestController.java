package com.example.searchservice.tag.controller;

import com.example.searchservice.tag.controller.swagger.TagSuggestControllerSwagger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search/tags")
public class TagSuggestController implements TagSuggestControllerSwagger {

    @GetMapping("/suggest")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> suggest(String q, int size) {
        return null;
    }
}
