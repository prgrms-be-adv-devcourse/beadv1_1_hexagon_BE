package com.example.searchservice.saga.events.tag;

import com.example.searchservice.tag.service.dto.ProfileTagDto;
import java.util.List;

public record TagInitEvent(
        List<ProfileTagDto> tags
) {

}
