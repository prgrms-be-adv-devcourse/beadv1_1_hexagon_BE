package com.example.searchservice.saga.mapper;

import com.example.searchservice.commission.entity.CommissionDocumentEntity;
import java.util.List;
import org.hexagon.core.events.commission.CommissionUpsertEvent;
import org.hexagon.core.vo.Commission;

public class CommissionMapper {

    public static CommissionDocumentEntity toCommissionDocument(CommissionUpsertEvent upsertEvent, List<String> tags) {
        return CommissionDocumentEntity.builder()
<<<<<<< HEAD
                .code(commission.code())
                .title(commission.title())
                .content(commission.content())
                .memberCode(commission.memberCode())
                .memberNickname(commission.memberNickname())
                .tags(commission.tags())
                .startedAt(commission.startedAt())
                .endedAt(commission.endedAt())
                .paymentType(commission.paymentType())
                .payAmount(commission.payAmount())
                .isOpen(commission.isOpen())
                .updatedAt(commission.updatedAt())
                .build();
    }

    public static CommissionDocumentEntity toCommissionDocument(CommissionCreatedEvent createdEvent) {
        return CommissionDocumentEntity.builder()
                .code(createdEvent.code())
                .title(createdEvent.title())
                .content(createdEvent.content())
                .memberCode(createdEvent.memberCode())
                .memberNickname(createdEvent.memberNickname())
                .tags(createdEvent.tags())
                .startedAt(createdEvent.startedAt())
                .endedAt(createdEvent.endedAt())
                .paymentType(createdEvent.paymentType())
                .payAmount(createdEvent.payAmount())
                .isOpen(createdEvent.isOpen())
                .updatedAt(createdEvent.updatedAt())
                .build();
    }

    public static CommissionDocumentEntity toCommissionDocument(CommissionUpdatedEvent updatedEvent) {
        return CommissionDocumentEntity.builder()
                .code(updatedEvent.code())
                .title(updatedEvent.title())
                .content(updatedEvent.content())
                .memberCode(updatedEvent.memberCode())
                .memberNickname(updatedEvent.memberNickname())
                .tags(updatedEvent.tags())
                .startedAt(updatedEvent.startedAt())
                .endedAt(updatedEvent.endedAt())
                .paymentType(updatedEvent.paymentType())
                .payAmount(updatedEvent.payAmount())
                .isOpen(updatedEvent.isOpen())
                .updatedAt(updatedEvent.updatedAt())
=======
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
>>>>>>> 024615f (feat(search): 사용하지 않는 Mapper 메소드 삭제 및 CommissionUpsertEvent + tags -> CommissionDocumentEntity 변환 메소드 구현)
                .build();
    }
}
