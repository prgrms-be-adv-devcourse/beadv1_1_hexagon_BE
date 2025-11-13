package com.example.searchservice.tag.mapper;

import com.example.searchservice.tag.entity.TagDocumentEntity;
import com.example.searchservice.tag.service.dto.ProfileTagDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.suggest.Completion;

@RequiredArgsConstructor
public class TagMapper {

    public static TagDocumentEntity toDocument(ProfileTagDto profileTagDto, List<String> aliases) {
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
