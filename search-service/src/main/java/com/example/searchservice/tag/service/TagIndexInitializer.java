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
public class TagIndexInitializer {

    private final RestTemplate restTemplate;
    private final TagRepository tagRepository;
    private final TagAliasLoadService tagAliasLoadService;

    @Value("${external.profile-service.url}")
    private String profileServiceUrl;

//    @EventListener(ApplicationReadyEvent.class)
//    public void initIndex() {
//        String getAllTagsUrl = profileServiceUrl + "/api/tags";
//        ResponseEntity<List<ProfileTagDto>> response = restTemplate.exchange(
//                getAllTagsUrl,
//                HttpMethod.GET,
//                null,
//                new ParameterizedTypeReference<>() {
//                }
//        );
//
//        List<ProfileTagDto> tags = response.getBody();
//
//        if (tags == null || tags.isEmpty()) {
//            throw new TagException(TagErrorCode.TAG_FETCH_FAILED);
//        }
//
//        for (ProfileTagDto tagDto : tags) {
//            List<String> aliases = tagAliasLoadService.getTagAlias(tagDto.skill());
//
//            TagDocumentEntity document = TagMapper.toDocument(tagDto, aliases);
//            tagRepository.save(document);
//        }
//    }

    // 테스트용 메소드
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
