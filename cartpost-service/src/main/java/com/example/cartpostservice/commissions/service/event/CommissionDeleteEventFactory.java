package com.example.cartpostservice.commissions.service.event;

import org.hexagon.core.events.commission.CommissionDeletedEvent;

public class CommissionDeleteEventFactory {

    public static CommissionDeletedEvent createEvent(String commissionCode) {
        return new CommissionDeletedEvent(
                commissionCode
        );
    }

}
