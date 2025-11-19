package com.example.searchservice.saga.mapper;

import com.example.searchservice.tag.entity.TagDocumentEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.vo.Tag;
import org.springframework.data.elasticsearch.core.suggest.Completion;

@RequiredArgsConstructor
public class TagMapper {

    public static TagDocumentEntity toDocument(Tag tag, List<String> aliases) {
        if (aliases == null || aliases.isEmpty()) {
            aliases = List.of(tag.skill());
        }

        Completion completion = new Completion(aliases);

        return TagDocumentEntity.builder()
                .code(tag.code())
                .skill(tag.skill())
                .skillSuggest(completion)
                .build();
    }
}
