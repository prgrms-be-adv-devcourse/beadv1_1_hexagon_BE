package com.example.searchservice.saga.events.selfpromotion;

import com.example.searchservice.selfpromotion.service.dto.ProfileSelfPromotionDto;
import java.util.List;

public record SelfPromotionInitEvent(
        List<ProfileSelfPromotionDto> selfPromotions
) {

}
