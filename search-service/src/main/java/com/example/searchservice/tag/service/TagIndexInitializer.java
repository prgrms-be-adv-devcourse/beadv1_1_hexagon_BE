package com.example.searchservice.tag.service;

import com.example.searchservice.tag.entity.TagDocumentEntity;
import com.example.searchservice.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
// 더미 데이터 삽입용 클래스
public class TagIndexInitializer {

    private final TagRepository tagRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initIndex() {
        tagRepository.save(TagDocumentEntity.builder()
                .code("tag-001")
                .skill("Spring")
                .skillSuggest(new Completion(new String[]{"Spring", "spring", "스프링"}))
                .build());

        tagRepository.save(TagDocumentEntity.builder()
                .code("tag-002")
                .skill("Spring Boot")
                .skillSuggest(new Completion(new String[]{"Spring Boot", "spring boot", "스프링 부트", "스프링부트"}))
                .build());

        tagRepository.save(TagDocumentEntity.builder()
                .code("tag-003")
                .skill("Java")
                .skillSuggest(new Completion(new String[]{"Java", "java", "자바"}))
                .build());

        tagRepository.save(TagDocumentEntity.builder()
                .code("tag-004")
                .skill("JavaScript")
                .skillSuggest(new Completion(new String[]{"JavaScript", "javascript", "js", "자바스크립트"}))
                .build());
    }
}
