package com.example.searchservice.tag.service;

import com.example.searchservice.tag.entity.TagDocumentEntity;
import com.example.searchservice.tag.exception.TagErrorCode;
import com.example.searchservice.tag.exception.TagException;
import com.example.searchservice.tag.repository.TagRepository;
import com.example.searchservice.tag.service.dto.ProfileTagDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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

    @Value("${external.profile-service.url}")
    private String getAllTagsUrl;

    @EventListener(ApplicationReadyEvent.class)
    public void initIndex() {
        ResponseEntity<List<ProfileTagDto>> response = restTemplate.exchange(
                getAllTagsUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        List<ProfileTagDto> tags = response.getBody();

        if (tags == null || tags.isEmpty()) {
            throw new TagException(TagErrorCode.TAG_FETCH_FAILED);
        }

        List<TagDocumentEntity> docs = tags.stream()
                .map(this::toDocument)
                .toList();

        tagRepository.saveAll(docs);
    }

    public TagDocumentEntity toDocument(ProfileTagDto profileTagDto) {
        List<String> aliases = tagAliasLoadService.getTagAlias(profileTagDto.skill());
        if (aliases == null || aliases.isEmpty()) {
            aliases = List.of(profileTagDto.skill());
        }

        Completion completion = new Completion(aliases);

        return TagDocumentEntity.builder()
                .code(profileTagDto.tagCode())
                .skill(profileTagDto.skill())
                .skillSuggest(completion)
                .build();
    }
}
