package com.example.searchservice.saga;

import com.example.searchservice.commission.entity.CommissionDocumentEntity;
import com.example.searchservice.commission.service.CommissionService;

import com.example.searchservice.saga.mapper.CommissionMapper;
import com.example.searchservice.saga.mapper.SelfPromotionMapper;
import com.example.searchservice.saga.mapper.TagMapper;
import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;
import com.example.searchservice.selfpromotion.service.SelfPromotionService;
import com.example.searchservice.tag.entity.TagDocumentEntity;
import com.example.searchservice.tag.service.TagAliasLoadService;
import com.example.searchservice.tag.service.TagService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.events.commission.CommissionDeletedEvent;
import org.hexagon.core.events.commission.CommissionInitEvent;
import org.hexagon.core.events.commission.CommissionUpsertEvent;
import org.hexagon.core.events.selfpromotion.SelfPromotionCreatedEvent;
import org.hexagon.core.events.selfpromotion.SelfPromotionDeletedEvent;
import org.hexagon.core.events.selfpromotion.SelfPromotionInitEvent;
import org.hexagon.core.events.selfpromotion.SelfPromotionUpdatedEvent;
import org.hexagon.core.events.tag.TagInitEvent;
import org.hexagon.core.vo.Tag;
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
    private final TagAliasLoadService tagAliasLoadService;

    @KafkaHandler
    public void handleEvent(@Payload TagInitEvent event) {
        List<Tag> tags = event.tags();
        List<TagDocumentEntity> docs = new ArrayList<>();

        for (Tag tag : tags) {
            // 별칭 사전(json)에서 별칭 데이터 불러옴
            List<String> aliases = tagAliasLoadService.getTagAlias(tag.skill());

            // Completion 필드에 별칭 데이터 추가
            TagDocumentEntity document = TagMapper.toDocument(tag, aliases);
            docs.add(document);
        }

        tagService.saveAll(docs);
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
        SelfPromotionDocumentEntity doc = SelfPromotionMapper.toSelfPromotionDocument(event);
        selfPromotionService.save(doc);
    }

    @KafkaHandler
    public void handleEvent(@Payload SelfPromotionUpdatedEvent event) {
        SelfPromotionDocumentEntity doc = SelfPromotionMapper.toSelfPromotionDocument(event);
        selfPromotionService.update(doc);
    }

    @KafkaHandler
    public void handleEvent(@Payload SelfPromotionDeletedEvent event) {
        selfPromotionService.delete(event.code());
    }

    // TODO: 의뢰글 인덱싱 Python 코드 작성 후 삭제
//    @KafkaHandler
//    public void handleEvent(@Payload CommissionInitEvent event) {
//        List<CommissionDocumentEntity> docs = event.commissions().stream()
//                .map(CommissionMapper::toCommissionDocument)
//                .toList();
//
//        commissionService.saveAll(docs);
//    }

    @KafkaHandler
    public void handleEvent(@Payload CommissionUpsertEvent event) {
        List<String> tags = tagService.findTagsByCodes(event.tagCodes());
        CommissionDocumentEntity doc = CommissionMapper.toCommissionDocument(event, tags);
        commissionService.update(doc);
    }

    @KafkaHandler
    public void handleEvent(@Payload CommissionDeletedEvent event) {
        commissionService.delete(event.code());
    }
}
