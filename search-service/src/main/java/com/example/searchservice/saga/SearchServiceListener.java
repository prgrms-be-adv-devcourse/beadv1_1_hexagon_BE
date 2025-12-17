package com.example.searchservice.saga;

import com.example.searchservice.commission.entity.CommissionDocumentEntity;
import com.example.searchservice.commission.service.CommissionService;

import com.example.searchservice.saga.mapper.CommissionMapper;
import com.example.searchservice.saga.mapper.SelfPromotionMapper;
import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;
import com.example.searchservice.selfpromotion.service.SelfPromotionService;
import com.example.searchservice.tag.service.TagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.events.commission.CommissionDeletedEvent;
import org.hexagon.core.events.commission.CommissionUpsertEvent;
import org.hexagon.core.events.selfpromotion.SelfPromotionDeletedEvent;
import org.hexagon.core.events.selfpromotion.SelfPromotionUpsertEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@KafkaListener(
        topics = {
                "${topics.tag-events}",
                "${topics.selfpromotion-events}",
                "${topics.commission-events}"
        },
        groupId = "search-service-group"
)
public class SearchServiceListener {

    private final SelfPromotionService selfPromotionService;
    private final CommissionService commissionService;
    private final TagService tagService;

    @KafkaHandler
    public void handleEvnet(@Payload SelfPromotionUpsertEvent event) {
        SelfPromotionDocumentEntity doc = SelfPromotionMapper.toSelfPromotionDocument(event);
        selfPromotionService.upsert(doc);
    }

    @KafkaHandler
    public void handleEvent(@Payload SelfPromotionDeletedEvent event) {
        selfPromotionService.delete(event.code());
    }

    @KafkaHandler
    public void handleEvent(@Payload CommissionUpsertEvent event) {
        List<String> tags = tagService.findTagsByCodes(event.tagCodes());
        CommissionDocumentEntity doc = CommissionMapper.toCommissionDocument(event, tags);
        commissionService.upsert(doc);
    }

    @KafkaHandler
    public void handleEvent(@Payload CommissionDeletedEvent event) {
        commissionService.delete(event.code());
    }
}
