package com.example.searchservice.saga.service;

import com.example.searchservice.tag.entity.TagDocumentEntity;
import com.example.searchservice.tag.repository.TagRepository;
import com.example.searchservice.tag.service.TagAliasLoadService;
import com.example.searchservice.tag.service.dto.ProfileTagDto;
import com.example.searchservice.tag.service.mapper.TagMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final TagAliasLoadService tagAliasLoadService;

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

