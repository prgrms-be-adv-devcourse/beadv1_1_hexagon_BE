package com.example.searchservice.saga.mapper;

import com.example.searchservice.commission.entity.CommissionDocumentEntity;
import java.util.List;
import org.hexagon.core.events.commission.CommissionUpsertEvent;
import org.hexagon.core.vo.Commission;

public class CommissionMapper {

    public static CommissionDocumentEntity toCommissionDocument(CommissionUpsertEvent upsertEvent, List<String> tags) {
        return CommissionDocumentEntity.builder()
                .code(upsertEvent.code())
                .title(upsertEvent.title())
                .content(upsertEvent.content())
                .memberCode(upsertEvent.memberCode())
                .memberNickname(upsertEvent.memberNickname())
                .tags(tags)
                .startedAt(upsertEvent.startedAt())
                .endedAt(upsertEvent.endedAt())
                .paymentType(upsertEvent.paymentType())
                .payAmount(upsertEvent.payAmount())
                .isClosed(upsertEvent.isClosed())
                .updatedAt(upsertEvent.updatedAt())
                .build();
    }
}
