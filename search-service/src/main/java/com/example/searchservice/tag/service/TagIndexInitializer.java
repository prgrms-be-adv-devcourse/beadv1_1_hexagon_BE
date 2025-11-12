package com.example.searchservice.tag.service;

import com.example.searchservice.tag.dto.TagDto;
import com.example.searchservice.tag.entity.TagDocumentEntity;
import com.example.searchservice.tag.exception.TagErrorCode;
import com.example.searchservice.tag.exception.TagException;
import com.example.searchservice.tag.repository.TagRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class TagIndexInitializer {

    private final RestTemplate restTemplate;
    private final TagRepository tagRepository;
    private final TagAliasLoadService tagAliasLoadService;

    @Value("${external.tag-service.url}")
    private String getAllTagsUrl;

    @PostConstruct
    public void initIndex() {
        ResponseEntity<List<TagDto>> response = restTemplate.exchange(
                getAllTagsUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        List<TagDto> tags = response.getBody();

        if (tags == null || tags.isEmpty()) {
            throw new TagException(TagErrorCode.TAG_FETCH_FAILED);
        }

        List<TagDocumentEntity> docs = tags.stream()
                .map(this::toDocument)
                .toList();

        tagRepository.saveAll(docs);
    }

    public TagDocumentEntity toDocument(TagDto tagDto) {
        List<String> aliases = tagAliasLoadService.getTagAlias(tagDto.skill());
        if (aliases == null || aliases.isEmpty()) {
            aliases = List.of(tagDto.skill());
        }

        Completion completion = new Completion(aliases);
//        completion.setWeight(100);

        return TagDocumentEntity.builder()
                .code(tagDto.code())
                .skill(tagDto.skill())
                .skillSuggest(completion)
                .build();
    }
}
