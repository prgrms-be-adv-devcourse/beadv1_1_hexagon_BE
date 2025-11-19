package com.example.searchservice.tag.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggest;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggester;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import co.elastic.clients.elasticsearch.core.search.Suggestion;
import com.example.searchservice.tag.dto.TagResponseDto;
import com.example.searchservice.tag.entity.TagDocumentEntity;
import com.example.searchservice.tag.exception.TagErrorCode;
import com.example.searchservice.tag.exception.TagException;
import com.example.searchservice.tag.repository.TagRepository;
import com.example.searchservice.tag.service.dto.ProfileTagDto;
import com.example.searchservice.tag.service.mapper.TagMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.vo.Tag;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final ElasticsearchClient esClient;
    private final TagAliasLoadService tagAliasLoadService;
    private final TagRepository tagRepository;

    @Override
    public List<TagResponseDto> getSuggestions(String prefix, int size) {
        try {
            CompletionSuggester completion = new CompletionSuggester.Builder()
                    .field("skill_suggest")
                    .size(size)
                    .fuzzy(f -> f
                            .fuzziness("1")
                            .unicodeAware(true))
                    .build();

            Suggester suggester = new Suggester.Builder()
                    .suggesters("tag-suggest", s -> s
                            .prefix(prefix)
                            .completion(completion))
                    .build();

            SearchResponse<TagDocumentEntity> response = esClient.search(s -> s
                            .index("tags")
                            .size(0)
                            .source(src -> src.filter(f -> f.includes("code", "skill")))
                            .suggest(suggester),
                    TagDocumentEntity.class
            );

            List<Suggestion<TagDocumentEntity>> bucket = response.suggest().get("tag-suggest");
            if (bucket == null || bucket.isEmpty()) return List.of();

            List<TagResponseDto> result = new ArrayList<>();

            for (Suggestion<TagDocumentEntity> s : bucket) {
                CompletionSuggest<TagDocumentEntity> completionResult = s.completion();
                if (completionResult == null) continue;

                completionResult.options().forEach(opt -> {
                    TagDocumentEntity src = opt.source();
                    String code  = (src != null && src.getCode() != null) ? src.getCode() : opt.id();
                    String skill = (src != null && src.getSkill() != null) ? src.getSkill() : opt.text();
                    result.add(new TagResponseDto(code, skill));
                });
            }
            return result;

        } catch (ElasticsearchException | IOException e) {
            throw new TagException(TagErrorCode.TAG_SUGGEST_FAILED, e);
        }
    }

    @Override
    public void saveAll(List<ProfileTagDto> tags) {
        for (ProfileTagDto tagDto : tags) {
            // 별칭 사전(json)에서 별칭 데이터 불러옴
            List<String> aliases = tagAliasLoadService.getTagAlias(tagDto.skill());

            // Completion 필드에 별칭 데이터 추가
            TagDocumentEntity document = TagMapper.toDocument(tagDto, aliases);
            tagRepository.save(document);
        }
    }
}
