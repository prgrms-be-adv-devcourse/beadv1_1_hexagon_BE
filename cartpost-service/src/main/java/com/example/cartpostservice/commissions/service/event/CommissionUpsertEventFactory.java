package com.example.cartpostservice.commissions.service.event;

import org.hexagon.core.events.commission.CommissionUpsertEvent;

public class CommissionUpsertEventFactory {

    public static CommissionUpsertEvent createEvent() {
        return new CommissionUpsertEvent(
                result.code(),
                result.title(),
                result.content(),
                result.memberCode(),
                result.writerName(),
                result.tags(),
                result.startedAt(),
                result.endedAt(),
                result.paymentType(),
                result.payAmount(),
                result.isClosed(),
                result.updatedAt()
        );
    }

}
