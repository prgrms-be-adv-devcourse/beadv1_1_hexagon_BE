package com.example.searchservice.saga;

import com.example.searchservice.commission.entity.CommissionDocumentEntity;
import com.example.searchservice.commission.service.CommissionService;
import com.example.searchservice.commission.service.mapper.CommissionMapper;
import com.example.searchservice.saga.events.commission.CommissionCreatedEvent;
import com.example.searchservice.saga.events.commission.CommissionDeletedEvent;
import com.example.searchservice.saga.events.commission.CommissionInitEvent;
import com.example.searchservice.saga.events.commission.CommissionUpdatedEvent;
import com.example.searchservice.saga.events.selfpromotion.SelfPromotionCreatedEvent;
import com.example.searchservice.saga.events.selfpromotion.SelfPromotionDeletedEvent;
import com.example.searchservice.saga.events.selfpromotion.SelfPromotionInitEvent;
import com.example.searchservice.saga.events.selfpromotion.SelfPromotionUpdatedEvent;
import com.example.searchservice.saga.events.tag.TagInitEvent;
import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;
import com.example.searchservice.selfpromotion.service.SelfPromotionService;
import com.example.searchservice.selfpromotion.service.mapper.SelfPromotionMapper;
import com.example.searchservice.tag.service.TagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    public void handleEvent(@Payload TagInitEvent event) {
        tagService.saveAll(event.tags());
    }

    @KafkaHandler
    public void handleEvent(@Payload SelfPromotionInitEvent event) {
        List<SelfPromotionDocumentEntity> docs = event.selfPromotions().stream()
                .map(SelfPromotionMapper::toSelfPromotionDocument)
                .toList();

        selfPromotionService.saveAll(docs);
    }

    @KafkaHandler
    public void handleEvent(@Payload SelfPromotionCreatedEvent event) {
        SelfPromotionDocumentEntity doc = SelfPromotionCreatedEvent.toDocumentEntity(event);
        selfPromotionService.save(doc);
    }

    @KafkaHandler
    public void handleEvent(@Payload SelfPromotionUpdatedEvent event) {
        SelfPromotionDocumentEntity doc = SelfPromotionUpdatedEvent.toDocumentEntity(event);
        selfPromotionService.update(doc);
    }

    @KafkaHandler
    public void handleEvent(@Payload SelfPromotionDeletedEvent event) {
        selfPromotionService.delete(event.code());
    }

    @KafkaHandler
    public void handleEvent(@Payload CommissionInitEvent event) {
        List<CommissionDocumentEntity> docs = event.commissions().stream()
                .map(CommissionMapper::toCommissionDocument)
                .toList();

        commissionService.saveAll(docs);
    }

    @KafkaHandler
    public void handleEvent(@Payload CommissionCreatedEvent event) {
        CommissionDocumentEntity doc = CommissionCreatedEvent.toCommissionDocumentEntity(event);
        commissionService.save(doc);
    }

    @KafkaHandler
    public void handleEvent(@Payload CommissionUpdatedEvent event) {
        CommissionDocumentEntity doc = CommissionUpdatedEvent.toCommissionDocumentEntity(event);
        commissionService.update(doc);
    }

    @KafkaHandler
    public void handleEvent(@Payload CommissionDeletedEvent event) {
        commissionService.delete(event.code());
    }
}
