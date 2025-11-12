package com.example.searchservice.tag.service;

import com.example.searchservice.tag.exception.TagErrorCode;
import com.example.searchservice.tag.exception.TagException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class TagAliasLoadService {

    private final Map<String, List<String>> tagAliasMap;

    public TagAliasLoadService() {
        try(InputStream inputStream =
                getClass().getClassLoader().getResourceAsStream("elasticsearch/tag-aliases.json")) {

            if (inputStream == null) {
                throw new TagException(TagErrorCode.TAG_ALIAS_LOAD_FAILED);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            tagAliasMap = objectMapper.readValue(
                    inputStream,
                    new TypeReference<>() {}
            );
        } catch (Exception e) {
            throw new TagException(TagErrorCode.TAG_ALIAS_LOAD_FAILED, e);
        }
    }

    public List<String> getTagAlias(String tagName){
        return tagAliasMap.get(tagName);
    }
}